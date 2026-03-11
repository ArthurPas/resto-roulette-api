package com.roulette.resto.dao.roulette;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roulette.resto.data.roulette.websocket.AccountChoices;
import com.roulette.resto.data.roulette.websocket.RouletteSession;
import com.roulette.resto.data.roulette.websocket.VetoResto;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Repository
public class RouletteDao {

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private final RedissonClient redissonClient;

	private static final String KEY_PREFIX = "roulette_session:";
	private static final String KEY_SHORT_PREFIX = "roulette_short:"; // Mapping Key
	private static final String LOCK_PREFIX = "lock:roulette_session:";

	public RouletteDao(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, RedissonClient redissonClient) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
		this.redissonClient = redissonClient;
	}

	public RouletteSession createSession(String sessionId) {
		String key = KEY_PREFIX + sessionId;

		String shortId = reserveUniqueShortId(sessionId);

		RouletteSession session = new RouletteSession();
		session.setSessionId(sessionId);
		session.setShortId(shortId);
		session.setAccountChoices(new ArrayList<>());

		try {
			String json = objectMapper.writeValueAsString(session);

			Boolean isCreated = redisTemplate.opsForValue().setIfAbsent(key, json);

			if (Boolean.FALSE.equals(isCreated)) {
				redisTemplate.delete(KEY_SHORT_PREFIX + shortId);
				throw new RuntimeException("Session " + sessionId + " already exists.");
			}
		} catch (JsonProcessingException e) {
			redisTemplate.delete(KEY_SHORT_PREFIX + shortId);
			throw new RuntimeException("Serialization error for session " + sessionId, e);
		}
		return session;
	}

	public String getSessionIdByShortId(String shortId) {
		String key = KEY_SHORT_PREFIX + shortId;
		return redisTemplate.opsForValue().get(key);
	}

	public void addAccountIdToSession(String sessionId, String login) {
		String key = KEY_PREFIX + sessionId;
		String lockKey = LOCK_PREFIX + sessionId;
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock();
		try {
			String json = redisTemplate.opsForValue().get(key);
			RouletteSession session;

			if (json != null && !json.isEmpty()) {
				session = objectMapper.readValue(json, RouletteSession.class);
			} else {
				session = new RouletteSession();
				session.setSessionId(sessionId);
				session.setAccountChoices(new ArrayList<>());
			}
			if (session.getAccountChoices() == null) {
				session.setAccountChoices(new ArrayList<>());
			}
			boolean exists = session.getAccountChoices().stream()
					.anyMatch(choice -> choice.getLogin() == login);

			if (!exists) {
				session.getAccountChoices().add(new AccountChoices(login, new HashSet<>(), new HashSet<>(),false));
				String updatedJson = objectMapper.writeValueAsString(session);
				redisTemplate.opsForValue().set(key, updatedJson);

				System.out.println(">>> SUCCESS : Ajout user " + login + " dans session " + sessionId);
			} else {
				System.out.println(">>> INFO : User " + login + " déjà présent.");
			}

		} catch (Exception e) {
			throw new RuntimeException("Error updating session " + sessionId, e);
		} finally {
			lock.unlock();
		}
	}

	public void addFoodChoices(String sessionId, AccountChoices choices) {
		String key = KEY_PREFIX + sessionId;
		String lockKey = LOCK_PREFIX + sessionId;

		RLock lock = redissonClient.getLock(lockKey);
		lock.lock();
		try {
			String json = redisTemplate.opsForValue().get(key);
			RouletteSession session;

			if (json != null && !json.isEmpty()) {
				session = objectMapper.readValue(json, RouletteSession.class);
			} else {
				session = new RouletteSession();
				session.setSessionId(sessionId);
				session.setAccountChoices(new ArrayList<>());
			}
			if (session.getAccountChoices() == null) {
				session.setAccountChoices(new ArrayList<>());
			}
			session.getAccountChoices().removeIf(ac -> Objects.equals(ac.getLogin(), choices.getLogin()));
			session.getAccountChoices().add(choices);
			String updatedJson = objectMapper.writeValueAsString(session);
			redisTemplate.opsForValue().set(key, updatedJson);

		} catch (Exception e) {
			throw new RuntimeException("Error updating choices for session " + sessionId, e);
		} finally {
			lock.unlock();
		}
	}

	public RouletteSession getSession(String sessionId) {
		String key = KEY_PREFIX + sessionId;
		String json = redisTemplate.opsForValue().get(key);
		if (json == null || json.isEmpty()) {
			return null;
		}

		try {
			return objectMapper.readValue(json, RouletteSession.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Impossible to read session " + sessionId, e);
		}
	}
	private String reserveUniqueShortId(String sessionId) {
		int maxRetries = 5;
		for (int i = 0; i < maxRetries; i++) {
			String shortId = generateRandomString(4);
			String mappingKey = KEY_SHORT_PREFIX + shortId;
			Boolean success = redisTemplate.opsForValue().setIfAbsent(mappingKey, sessionId);

			if (Boolean.TRUE.equals(success)) {
				return shortId;
			}
		}
		throw new RuntimeException("Failed to generate unique shortId after retries");
	}

	private String generateRandomString(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int index = ThreadLocalRandom.current().nextInt(chars.length());
			sb.append(chars.charAt(index));
		}
		return sb.toString();
	}

	public List<AccountChoices> getChoicesBySession(String sessionId) {
		if (sessionId == null) {
			throw new IllegalArgumentException("Session ID cannot be null");
		}

		String key = KEY_PREFIX + sessionId;
		String jsonValue = redisTemplate.opsForValue().get(key);

		if (jsonValue == null) {
			return Collections.emptyList();
		}

		try {
			RouletteSession sessionData = objectMapper.readValue(jsonValue, RouletteSession.class);
			return sessionData.getAccountChoices();
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON from Redis", e);
		}
	}

	public Set<Integer> removeRestoFromSession(String sessionId, VetoResto vetoResto) {
		String key = KEY_PREFIX + sessionId;
		String lockKey = LOCK_PREFIX + sessionId;
		RLock lock = redissonClient.getLock(lockKey);

		lock.lock();
		try {
			RouletteSession session = getSession(sessionId);
			if (session == null) return Collections.emptySet();

			if (session.getRestoIds() != null) {
				session.getRestoIds().removeAll(vetoResto.getRestoIds());
			}

			session.getAccountChoices().stream()
					.filter(ac -> ac.getLogin() == vetoResto.getLogin())
					.findFirst()
					.ifPresent(ac -> ac.setVetoDone(true));

			String updatedJson = objectMapper.writeValueAsString(session);
			redisTemplate.opsForValue().set(key, updatedJson);

			return session.getRestoIds();
		} catch (Exception e) {
			throw new RuntimeException("Transaction failed", e);
		} finally {
			lock.unlock();
		}
	}
	public void saveMatchingRestos(Set<Integer> restoIds, String sessionId) {
		String key = KEY_PREFIX + sessionId;
		String lockKey = LOCK_PREFIX + sessionId;
		RLock lock = redissonClient.getLock(lockKey);

		lock.lock();
		try {
			String json = redisTemplate.opsForValue().get(key);
			RouletteSession session;

			if (json != null && !json.isEmpty()) {
				session = objectMapper.readValue(json, RouletteSession.class);
			} else {
				session = new RouletteSession();
				session.setSessionId(sessionId);
			}

			if (session.getRestoIds() == null) {
				session.setRestoIds(restoIds);
			} else {
				session.getRestoIds().addAll(restoIds);
			}

			String updatedJson = objectMapper.writeValueAsString(session);
			redisTemplate.opsForValue().set(key, updatedJson);

		} catch (Exception e) {
			throw new RuntimeException("Error updating session " + sessionId, e);
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	public Set<Integer> getRestosBySession(String sessionId) {
		String key = KEY_PREFIX + sessionId;
		try {
			String json = redisTemplate.opsForValue().get(key);
			if (json == null || json.isEmpty()) {
				return new HashSet<>();
			}
			RouletteSession session = objectMapper.readValue(json, RouletteSession.class);
			if (session.getRestoIds() == null) {
				return new HashSet<>();
			}

			return session.getRestoIds();

		} catch (Exception e) {
			throw new RuntimeException("Error fetching restos for session " + sessionId, e);
		}
	}

	public void setVetoStatusForAccount(String sessionId, String login, boolean status) {
		String key = KEY_PREFIX + sessionId;
		String lockKey = LOCK_PREFIX + sessionId;

		RLock lock = redissonClient.getLock(lockKey);
		lock.lock();
		try {
			String json = redisTemplate.opsForValue().get(key);
			if (json == null || json.isEmpty()) {
				return;
			}
			RouletteSession session = objectMapper.readValue(json, RouletteSession.class);
			if (session.getAccountChoices() != null) {
				session.getAccountChoices().stream()
						.filter(ac -> Objects.equals(ac.getLogin(), login))
						.findFirst()
						.ifPresent(ac -> {
							ac.setVetoDone(status);
						});
				String updatedJson = objectMapper.writeValueAsString(session);
				log.info(updatedJson);
				redisTemplate.opsForValue().set(key, updatedJson);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error updating veto status for account " + login + " in session " + sessionId, e);
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
}