# Jenkins LTS (JDK 17) + pre-installed Gradle 8 + plugins from plugins.txt.
# JDK 17 is required by io.freefair.lombok 6.0.0-m2; Gradle 8 is required
# because Gradle 9 dropped JavaPluginConvention which the lombok plugin uses.

FROM jenkins/jenkins:lts-jdk17

ARG GRADLE_VERSION=8.14.4
ARG ALLURE_VERSION=2.30.0

USER root
RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends curl unzip ca-certificates python3; \
    rm -rf /var/lib/apt/lists/*; \
    # Gradle
    curl -fsSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o /tmp/gradle.zip; \
    unzip -q /tmp/gradle.zip -d /opt; \
    ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/local/bin/gradle; \
    rm /tmp/gradle.zip; \
    gradle --version | head -3; \
    # Allure CLI (used by allure-jenkins-plugin to render reports)
    curl -fsSL "https://github.com/allure-framework/allure2/releases/download/${ALLURE_VERSION}/allure-${ALLURE_VERSION}.tgz" -o /tmp/allure.tgz; \
    tar -xzf /tmp/allure.tgz -C /opt; \
    ln -s /opt/allure-${ALLURE_VERSION}/bin/allure /usr/local/bin/allure; \
    rm /tmp/allure.tgz; \
    allure --version
USER jenkins

# Pre-install plugins. Jenkins reads /usr/share/jenkins/ref/plugins.txt at
# image build time via jenkins-plugin-cli (provided by the base image).
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

# Skip the setup wizard — JCasC handles initial config.
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
ENV CASC_JENKINS_CONFIG=/var/jenkins_home/casc/casc.yaml
