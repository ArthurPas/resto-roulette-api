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

deploy:
	ansible-playbook ansible/playbooks/deploy-app.yml --ask-vault-pass

init:
	ansible-playbook ansible/playbooks/init-app.yml --ask-vault-pass

init-db:
	ansible-playbook ansible/playbooks/init-db.yml --ask-vault-pass