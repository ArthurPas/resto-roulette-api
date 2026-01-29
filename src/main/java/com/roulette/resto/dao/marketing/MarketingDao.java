package com.roulette.resto.dao.marketing;

import com.roulette.resto.data.marketing.MarketingCampaignInfo;
import com.roulette.resto.data.marketing.MarketingMapper;
import com.roulette.resto.data.marketing.NewMarketingCampaign;
import com.roulette.resto.data.social.mapper.ActivityRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
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
             INSERT INTO sponso_campaign (resto_id, start_date, expiration_date, media_id, description, post_location)
             VALUES (?, ?, ?, ?, ?, ?)
             """;
		try {
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setInt(1, campaign.getRestoId());
				preparedStatement.setDate(2, campaign.getStartDate());
				preparedStatement.setDate(3, campaign.getExpirationDate());
				preparedStatement.setString(4, campaign.getMediaId());
				preparedStatement.setString(5, campaign.getDescription());
				preparedStatement.setString(6, campaign.getLocation().name());
				log.debug(preparedStatement.toString());
				return preparedStatement;
			}, generatedKeyHolder);
			return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
		} catch (DuplicateKeyException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public MarketingCampaignInfo getCampaignById(Integer campaignId) {
		String query ="""	
					SELECT campaign_id, resto_id, start_date, expiration_date, media_id, description, post_location
					FROM sponso_campaign WHERE campaign_id = ?
					""";
		try {
			return jdbcTemplate.query(
					connection -> {
						PreparedStatement preparedStatement = connection.prepareStatement(query);
						preparedStatement.setInt(1, campaignId);
						log.debug(preparedStatement.toString());
						return preparedStatement;
					},
					new MarketingMapper()).getFirst();
		} catch (NoSuchElementException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public void incrementViews(int campaignId, int count) {
		String query = """
					  UPDATE sponso_campaign SET view = view + ? WHERE campaign_id = ?;
					""";
		try {
			jdbcTemplate.update(conn -> {
				PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setInt(1, count);
				preparedStatement.setInt(2, campaignId);
				log.debug(preparedStatement.toString());
				return preparedStatement;
			});
		}catch (NoSuchElementException e){
			log.error(e.getMessage());
		}
	}

	public List<Integer> findAllActiveIds() {
		String query = """
             SELECT campaign_id
             FROM sponso_campaign 
             WHERE expiration_date > ?
             """;

		try {
			return jdbcTemplate.query(
					query,
					ps -> ps.setTimestamp(1, Timestamp.from(Instant.now())),
					new SingleColumnRowMapper<>(Integer.class)
			);
		} catch (Exception e) {
			log.error("Error fetching active campaign IDs: {}", e.getMessage());
			throw e;
		}
	}
}
