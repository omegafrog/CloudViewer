# Suggested Commands
- List files: `ls`, `rg --files`
- Run architecture rules: `GRADLE_USER_HOME=/mnt/e/workspace/cloudViewer/.gradle GRADLE_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false" ./gradlew --no-daemon architectureRules`
- Run unit tests (module): `GRADLE_USER_HOME=/mnt/e/workspace/cloudViewer/.gradle GRADLE_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false" ./gradlew --no-daemon :modules:plugin-runtime:test`
- Run core tests: `GRADLE_USER_HOME=/mnt/e/workspace/cloudViewer/.gradle GRADLE_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false" ./gradlew --no-daemon :modules:core:test`
- Run app (core): `GRADLE_USER_HOME=/mnt/e/workspace/cloudViewer/.gradle GRADLE_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false" ./gradlew --no-daemon :modules:core:bootRun`
- Search: `rg <pattern>`