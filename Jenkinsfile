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

            // Upload allure-results to Allure TestOps as a new launch.
            // Skipped silently if creds are missing/placeholder.
            // --allow-insecure-connection: temporary while QA Guru's SSL cert is
            // being renewed (May 2026). Remove once allure.autotests.cloud has a
            // valid cert.
            script {
                catchError(buildResult: currentBuild.currentResult, stageResult: 'SUCCESS') {
                    withCredentials([
                        string(credentialsId: 'allure-testops-endpoint',   variable: 'ALLURE_ENDPOINT'),
                        string(credentialsId: 'allure-testops-token',      variable: 'ALLURE_TOKEN'),
                        string(credentialsId: 'allure-testops-project-id', variable: 'ALLURE_PROJECT_ID')
                    ]) {
                        sh '''
                            set +e
                            if [ ! -d "build/allure-results" ] || [ "$ALLURE_TOKEN" = "placeholder" ] || [ -z "$ALLURE_TOKEN" ]; then
                                echo "Skip Allure TestOps upload (no results or no token)"
                                exit 0
                            fi
                            allurectl --allow-insecure-connection upload \
                                --endpoint "$ALLURE_ENDPOINT" \
                                --token "$ALLURE_TOKEN" \
                                --project-id "$ALLURE_PROJECT_ID" \
                                --launch-name "${JOB_NAME} #${BUILD_NUMBER}" \
                                build/allure-results || echo "allurectl upload failed (continuing)"
                            exit 0
                        '''
                    }
                }
            }

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
                // Parse JUnit XML for counts + failed test names. Python is reliable
                // for XML; falls back to "0 0 0" if no results yet.
                def parsed = sh(returnStdout: true, script: '''
                    python3 - <<'PY'
import xml.etree.ElementTree as ET
import glob, sys
total = failed = skipped = 0
fails = []
for f in glob.glob('build/test-results/test/*.xml'):
    try:
        for tc in ET.parse(f).getroot().iter('testcase'):
            total += 1
            if tc.find('failure') is not None or tc.find('error') is not None:
                failed += 1
                cls = tc.attrib.get('classname', '').split('.')[-1]
                name = tc.attrib.get('name', '')
                line = f'{cls} > {name}'.replace('\\n', ' ').replace('\\r', ' ')
                fails.append(line[:80])
            elif tc.find('skipped') is not None:
                skipped += 1
    except Exception as e:
        print(f'parse err {f}: {e}', file=sys.stderr)
print(f'{total} {failed} {skipped}')
for line in fails[:5]:
    print(line)
PY
                ''').trim()
                def lines = parsed ? parsed.split('\n') : ['0 0 0']
                def hdr = lines[0].trim().split(/\s+/)
                int total   = (hdr.size() > 0 ? hdr[0] as int : 0)
                int failed  = (hdr.size() > 1 ? hdr[1] as int : 0)
                int skipped = (hdr.size() > 2 ? hdr[2] as int : 0)
                int passed  = total - failed - skipped
                def failNames = lines.size() > 1 ? lines[1..-1] : []

                def status = currentBuild.currentResult ?: 'UNKNOWN'
                def emoji  = [
                    SUCCESS:  '✅',
                    FAILURE:  '❌',
                    UNSTABLE: '⚠️',
                    ABORTED:  '⏹️'
                ].get(status, 'ℹ️')

                // Duration in pure long arithmetic (no BigDecimal mod, sandbox-safe).
                long durMs = ((currentBuild.duration ?: (System.currentTimeMillis() - currentBuild.startTimeInMillis)) as long)
                long durSec = durMs.intdiv(1000L)
                long msPart = durMs - durSec * 1000L
                long h = durSec.intdiv(3600L)
                long m = (durSec - h * 3600L).intdiv(60L)
                long s = durSec - h * 3600L - m * 60L
                def durFmt = String.format('%02d:%02d:%02d.%03d', h, m, s, msPart)

                def passedPctStr = total > 0 ? String.format('%.1f', passed * 100.0d / total) : '0.0'

                // Donut: title = "<JOB_NAME> #<BUILD>". Total in center.
                def chartTitle = "${env.JOB_NAME} #${env.BUILD_NUMBER}"
                def chartJson = """{"type":"doughnut","data":{"labels":["passed","failed","skipped"],"datasets":[{"data":[${passed},${failed},${skipped}],"backgroundColor":["#4caf50","#f44336","#9e9e9e"],"borderWidth":0}]},"options":{"title":{"display":true,"text":"${chartTitle}","fontSize":18},"plugins":{"doughnutlabel":{"labels":[{"text":"${total}","font":{"size":60,"weight":"bold"}}]},"datalabels":{"display":false}},"legend":{"position":"right"},"cutoutPercentage":70}}"""
                def chartUrl = 'https://quickchart.io/chart?width=600&height=400&c=' +
                               java.net.URLEncoder.encode(chartJson, 'UTF-8')

                // Build a richer caption: status + counts + failure list + 2 links.
                // Use list.add() + join — sandbox blocks StringBuilder.leftShift (<<).
                def lines2 = []
                lines2.add("${emoji} *${status}* — `${env.JOB_NAME}` #${env.BUILD_NUMBER}")
                lines2.add("*Suite:* `${params?.TEST_SUITE ?: 'test'}`")
                lines2.add("*Duration:* ${durFmt}")
                lines2.add("")
                lines2.add("📊 *Results* (${total} total):")
                lines2.add("  ✅ passed: *${passed}* (${passedPctStr}%)")
                if (failed > 0)  lines2.add("  ❌ failed: *${failed}*")
                if (skipped > 0) lines2.add("  ⏭ skipped: *${skipped}*")
                if (failNames && failNames.size() > 0 && failNames[0]) {
                    lines2.add("")
                    lines2.add("*Failures:*")
                    failNames.each { String name ->
                        if (name?.trim()) lines2.add("  • `${name}`")
                    }
                    if (failed > failNames.size()) {
                        lines2.add("  …и ещё ${failed - failNames.size()}")
                    }
                }
                lines2.add("")
                lines2.add("🔗 [Build](${env.BUILD_URL}) · [Allure](${env.BUILD_URL}allure) · [Console](${env.BUILD_URL}console)")

                def caption = lines2.join('\n')

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
