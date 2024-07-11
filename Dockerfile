FROM icr.io/appcafe/open-liberty:full-java21-openj9-ubi-minimal
#FROM icr.io/appcafe/open-liberty:kernel-slim-java21-openj9-ubi-minimal

# Add a Liberty server configuration including all necessary features
COPY --chown=1001:0  src/main/liberty/config/server.xml /config/
# Copy ssl data
COPY --chown=1001:0  verify-host-ssl/client/client.ks /opt/ol/wlp/output/defaultServer/
COPY --chown=1001:0  verify-host-ssl/client/client.ts /opt/ol/wlp/output/defaultServer/

# This script adds the requested XML snippets to enable Liberty features and grow the image to be fit-for-purpose.
# This option is available only in the 'kernel-slim' image type. The 'full' and 'beta' tags already include all features.
# RUN features.sh

# Add an application
COPY --chown=1001:0  target/LibertyMutualTlsForAMQBroker.war /config/dropins/

# This script adds the requested server configuration, applies any interim fixes, and populates caches to optimize the runtime.
# RUN configure.sh