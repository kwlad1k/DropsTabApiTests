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
            // Publish JUnit XML so the script block below can read pass/fail counts.
            junit allowEmptyResults: true,
                  skipPublishingChecks: true,
                  testResults: 'build/test-results/test/*.xml'

            // Allure report — published from build/allure-results.
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'build/allure-results']]
            ])

            // Telegram notification with QA-Guru-style donut chart via QuickChart.io.
            // Best-effort — missing creds / network errors do NOT fail the build.
            script {
                // Parse JUnit XML directly (avoids pipeline-sandbox restrictions on
                // currentBuild.rawBuild.getAction).
                def counts = sh(returnStdout: true, script: '''
                    set +e
                    cd build/test-results/test 2>/dev/null || { echo "0 0 0"; exit 0; }
                    total=$(grep -ho '<testcase' *.xml 2>/dev/null | wc -l | tr -d ' ')
                    failed=$(grep -ho '<failure' *.xml 2>/dev/null | wc -l | tr -d ' ')
                    skipped=$(grep -ho '<skipped' *.xml 2>/dev/null | wc -l | tr -d ' ')
                    echo "${total:-0} ${failed:-0} ${skipped:-0}"
                ''').trim().split()
                def total   = counts[0] as int
                def failed  = counts[1] as int
                def skipped = counts[2] as int
                def passed  = total - failed - skipped

                def status = currentBuild.currentResult ?: 'UNKNOWN'
                def emoji  = [
                    SUCCESS:  '✅',
                    FAILURE:  '❌',
                    UNSTABLE: '⚠️',
                    ABORTED:  '⏹️'
                ].get(status, 'ℹ️')

                // Format duration as HH:mm:ss.SSS to match QA-Guru output.
                def durMs = currentBuild.duration ?: (System.currentTimeMillis() - currentBuild.startTimeInMillis)
                def durFmt = String.format('%02d:%02d:%02d.%03d',
                    (long)(durMs / 3600000),
                    (long)((durMs / 60000) % 60),
                    (long)((durMs / 1000) % 60),
                    (long)(durMs % 1000))

                def passedPct = total > 0 ? (passed * 100.0d / total).round(1) : 0

                // QuickChart.io donut: green/red/grey wedges, total in center,
                // legend on the right, job name as title.
                def chartJson = """{"type":"doughnut","data":{"labels":["passed","failed","skipped"],"datasets":[{"data":[${passed},${failed},${skipped}],"backgroundColor":["#4caf50","#f44336","#9e9e9e"],"borderWidth":0}]},"options":{"title":{"display":true,"text":"${env.JOB_NAME}","fontSize":18},"plugins":{"doughnutlabel":{"labels":[{"text":"${total}","font":{"size":60,"weight":"bold"}}]},"datalabels":{"display":false}},"legend":{"position":"right"},"cutoutPercentage":70}}"""
                def chartUrl = 'https://quickchart.io/chart?width=600&height=400&c=' +
                               java.net.URLEncoder.encode(chartJson, 'UTF-8')

                def caption = """${emoji} *${status}*
*Results:*
*Environment:* Окружение Prod, коллекция тестов `${params?.TEST_SUITE ?: 'test'}`
*Comment:* Результат API тестов
*Duration:* ${durFmt}
*Total scenarios:* ${total}
*Total passed:* ${passed} (${passedPct} %)
*Report available at the link:* ${env.BUILD_URL}allure"""

                catchError(buildResult: currentBuild.currentResult, stageResult: 'SUCCESS') {
                    withCredentials([
                        string(credentialsId: 'telegram-bot-token', variable: 'TG_TOKEN'),
                        string(credentialsId: 'telegram-chat-id',   variable: 'TG_CHAT_ID')
                    ]) {
                        withEnv(["TG_PHOTO=${chartUrl}", "TG_CAPTION=${caption}"]) {
                            sh '''
                                set +e
                                curl -sS --max-time 20 \
                                    -X POST "https://api.telegram.org/bot${TG_TOKEN}/sendPhoto" \
                                    --data-urlencode "chat_id=${TG_CHAT_ID}" \
                                    --data-urlencode "photo=${TG_PHOTO}" \
                                    --data-urlencode "parse_mode=Markdown" \
                                    --data-urlencode "caption=${TG_CAPTION}" \
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
