package com.roulette.resto.dao.marketing;

import com.roulette.resto.data.marketing.NewMarketingCampaign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
@Repository
@Transactional
@Slf4j
public class MarketingDao {

	final JdbcTemplate jdbcTemplate;

	public MarketingDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int createSponsoCampaign(NewMarketingCampaign campaign) {
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

		String query = """
             INSERT INTO sponso_campaign (resto_id, start_date, expiration_date, ressource_id, description)
             VALUES (?, ?, ?, ?, ?)
             """;
		try {
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setInt(1, campaign.getRestoId());
				preparedStatement.setDate(2, campaign.getStartDate());
				preparedStatement.setDate(3, campaign.getExpirationDate());
				preparedStatement.setString(4, campaign.getMediaId());
				preparedStatement.setString(5, campaign.getDescription());
				log.debug(preparedStatement.toString());
				return preparedStatement;
			}, generatedKeyHolder);
			return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
		} catch (DuplicateKeyException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
}
