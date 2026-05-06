// =============================================================================
//  DropsTab API Tests — Jenkins pipeline
// =============================================================================
//
//  Required Jenkins "Secret text" credentials (Manage Jenkins → Credentials):
//
//    dropstab-user-name      DropsTab username           (e.g. "pidiri207")
//    dropstab-user-email     DropsTab email
//    dropstab-user-password  DropsTab password
//    dropstab-user-token     Bearer accessToken from POST /portfolio/login
//    dropstab-portfolio-id   id.portfolio override       (e.g. "2193621")
//    telegram-bot-token      BotFather token             (e.g. "1234:AA…")
//    telegram-chat-id        Target chat / channel id    (e.g. "-1001234567890")
//
//  Required Jenkins plugins:
//    - Pipeline                       (declarative)
//    - Credentials Binding
//    - Allure Jenkins Plugin
//    - AnsiColor (optional)
//    - Timestamper (optional)
//
//  Agent must have JDK 17 and Gradle 8.x on PATH. If the agent's defaults
//  differ, uncomment the `tools` block and configure matching entries in
//  Manage Jenkins → Tools.
// =============================================================================

pipeline {
    agent any

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['test', 'positive_test', 'negative_test', 'auth_test', 'portfolio_test', 'transaction_test'],
            description: 'Gradle task to run. "test" = full suite. The others filter by JUnit @Tag (see build.gradle).'
        )
    }

    options {
        timestamps()
        ansiColor('xterm')
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '20'))
    }

    // tools {
    //     jdk    'jdk17'
    //     gradle 'gradle8'
    // }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run tests') {
            steps {
                withCredentials([
                    string(credentialsId: 'dropstab-user-name',     variable: 'USER_NAME_DT'),
                    string(credentialsId: 'dropstab-user-email',    variable: 'USER_EMAIL'),
                    string(credentialsId: 'dropstab-user-password', variable: 'USER_PASSWORD'),
                    string(credentialsId: 'dropstab-user-token',    variable: 'USER_TOKEN'),
                    string(credentialsId: 'dropstab-portfolio-id',  variable: 'PORTFOLIO_ID')
                ]) {
                    // Single-quoted heredoc → no Groovy interpolation, secrets stay
                    // as shell variables and never appear in build logs verbatim.
                    sh '''
                        set -eu
                        gradle --no-daemon clean "${TEST_SUITE}" \
                            -Duser.name.dt="${USER_NAME_DT}" \
                            -Duser.email="${USER_EMAIL}" \
                            -Duser.password="${USER_PASSWORD}" \
                            -Duser.token="${USER_TOKEN}" \
                            -Did.portfolio="${PORTFOLIO_ID}"
                    '''
                }
            }
        }
    }

    post {
        always {
            // Allure report — published from build/allure-results.
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'build/allure-results']]
            ])

            // Telegram notification (best-effort; failure here doesn't fail the build).
            script {
                def status = currentBuild.currentResult ?: 'UNKNOWN'
                def emoji  = [
                    SUCCESS:  '✅',
                    FAILURE:  '❌',
                    UNSTABLE: '⚠️',
                    ABORTED:  '⏹️'
                ].get(status, 'ℹ️')

                def msg = """*DropsTab API Tests* ${emoji} *${status}*
Suite: `${params.TEST_SUITE}`
Build: [#${env.BUILD_NUMBER}](${env.BUILD_URL})
Allure: [report](${env.BUILD_URL}allure)"""

                // Best-effort — missing telegram-* credentials, network failure,
                // or Telegram API errors do NOT fail the build.
                catchError(buildResult: currentBuild.currentResult, stageResult: 'SUCCESS') {
                    withCredentials([
                        string(credentialsId: 'telegram-bot-token', variable: 'TG_TOKEN'),
                        string(credentialsId: 'telegram-chat-id',   variable: 'TG_CHAT_ID')
                    ]) {
                        // Pass the message via TG_MSG env so curl can URL-encode it
                        // — keeps Markdown / newlines / backticks intact.
                        withEnv(["TG_MSG=${msg}"]) {
                            sh '''
                                set +e
                                curl -sS --max-time 15 \
                                    -X POST "https://api.telegram.org/bot${TG_TOKEN}/sendMessage" \
                                    --data-urlencode "chat_id=${TG_CHAT_ID}" \
                                    --data-urlencode "parse_mode=Markdown" \
                                    --data-urlencode "disable_web_page_preview=true" \
                                    --data-urlencode "text=${TG_MSG}" \
                                    > /dev/null
                                exit 0
                            '''
                        }
                    }
                }
            }
        }
    }
}
