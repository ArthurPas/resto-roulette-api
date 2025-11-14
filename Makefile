COMPOSE = docker compose

start:
	doppler run -- $(COMPOSE) up --build

stop:
	$(COMPOSE) down

reset:
	$(COMPOSE) down -v

logs:
	$(COMPOSE) logs -f

rebuild:
	$(COMPOSE) build --no-cache

ps:
	$(COMPOSE) ps