import java.io.File

fun cloneRepository(repositoryUrl: String) {
    val projectName = repositoryUrl.split("/").last()
    // 프로젝트의 루트 디렉터리 경로를 얻습니다.
    val projectRoot = System.getProperty("user.dir")
    // clone할 디렉터리의 절대 경로를 생성합니다.
    val cloneDirectoryPath = "$projectRoot/repository/$projectName"

    try {
        // 지정된 디렉터리가 없다면 생성합니다.
        val directory = File(cloneDirectoryPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val process = ProcessBuilder("git", "clone", repositoryUrl, cloneDirectoryPath)
            .directory(directory) // 프로세스의 작업 디렉터리를 설정합니다.
            .start()

        process.waitFor() // 프로세스가 완료될 때까지 기다립니다.
        if (process.exitValue() == 0) {
            println("Repository cloned successfully into $cloneDirectoryPath")
        } else {
            println("Error cloning repository. Exit code: ${process.exitValue()}")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun main() {
    val repositoryUrl = "https://github.com/codingmate/mssql" // 클론하려는 레포지토리의 URL

    cloneRepository(repositoryUrl)
}
