# This file is generated by Nx.
#
# Build the docker image with `npx nx docker-build home-ops`.
# Tip: Modify "docker-build" options in project.json to change docker build args.
#
# Run the container with `docker run -p 3000:3000 -t home-ops`.
FROM docker.io/node:lts-alpine

ENV HOST=0.0.0.0
ENV PORT=3000

WORKDIR /app

RUN addgroup --system home-ops && \
          adduser --system -G home-ops home-ops

COPY dist/apps/home-ops home-ops/
RUN chown -R home-ops:home-ops .

# You can remove this install step if you build with `--bundle` option.
# The bundled output will include external dependencies.
RUN npm --prefix home-ops --omit=dev -f install

CMD [ "node", "home-ops" ]
