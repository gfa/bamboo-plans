# Call these functions before/after each target to maintain a coherent
# display
START_TARGET = @printf -- "\033[38;5;33m%s\033[0m\n" "$(1)"
END_TARGET = @printf "\033[38;5;46mOK\033[0m\n\n"


help: ## Display list of targets and their documentation
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk \
		'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

test: ## Check the syntax of the plans
	$(call START_TARGET,Checking plan syntax)
	@for DIR in $$(ls); do \
        if [ -d $${DIR} ]; then \
            echo "processing folder $${DIR}"; \
            $(MAKE) -C $${DIR} test; \
        fi; \
	    done;
	$(call END_TARGET)

apply: ## Apply the plans
	$(call START_TARGET,Applying the plans)
	@for DIR in $$(ls); do \
        if [ -d $${DIR} ]; then \
            echo "processing folder $${DIR}"; \
            cp -f /data/bamboo/.credentials $${DIR}/ ;\
            $(MAKE) -C $${DIR} apply; \
        fi; \
	    done;
	$(call END_TARGET)

generate-template:
	mvn archetype:generate -B -DarchetypeGroupId=com.atlassian.bamboo -DarchetypeArtifactId=bamboo-specs-archetype \
	-DarchetypeVersion=6.1.0 -DgroupId=com.atlassian.bamboo -DartifactId=bamboo-specs-tutorial -Dversion=1.0.0-SNAPSHOT \
	-Dpackage=tutorial -Dtemplate=minimal

create-template-python2: ## Create a plan based on the Python2 template
ifndef NAME
	$(error NAME is missing)
endif
	cp -r .python2 $(NAME)

create-template-python3: ## Create a plan based on the Python3 template
ifndef NAME
	$(error NAME is missing)
endif
	cp -r .python3 $(NAME)


.PHONY: test apply generate-template
