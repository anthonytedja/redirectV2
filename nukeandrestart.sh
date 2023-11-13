#!/bin/bash

echo "Nuking all docker stuff (it's being run in parallel so idk when it's finished, just wait a few seconds I guess)"
./forallvm "yes | docker system prune -a > /dev/null && echo ilovekevin409 | sudo -S service docker restart > /dev/null 2> /dev/null"