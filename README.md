Bamboo Configuration as code
============================

This is an example of a repo that contains Bamboo configuration as code.
New plans and projects are deployed from within the repo, just run `make NAME=my_awesome_app  generate-template-python[2-3]` edit the results, commit and push
Bamboo will receive the new instructions, configure the new plans and start to build them.

This plan expects the file `/data/bamboo/.credentials` with valid credentials to authenticate against the server.
