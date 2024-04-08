import java.io.File
fun main() {
    ReadmeGenerator().cloneRepository("https://github.com/codingmate/leetcode.git")
}
class ReadmeGenerator {
    fun cloneRepository(repositoryHttpsUrl: String) {

        val repositoryPath = repositoryHttpsUrl.substringAfterLast("https://github.com/")
        val repositoryHost = repositoryPath.substringBeforeLast("/")
        val repositoryName = repositoryPath.substringAfterLast("/").substringBeforeLast(".")
        val cloneDirectory = "./repository/$repositoryHost/$repositoryName"

        val pb = ProcessBuilder("git", "clone", repositoryHttpsUrl, cloneDirectory)
        pb.directory(File(System.getProperty("user.dir")))
        pb.inheritIO()

        try {
            val process = pb.start()
            process.waitFor()
        } catch (e: Exception) {
            println("Repository cloning failed: ${e.message}")
        }

        // 프로젝트 루트 디렉터리에서, "./repository/{repository이름}"에 clone
    }

    fun createReadmeInDirectory(directoryPath: String) {
        val directory = File(directoryPath)

        if (!directory.exists())
            directory.mkdirs()

        val readmeFile = File(directory, "readme.md")
        if (!readmeFile.exists())
            readmeFile.createNewFile()
    }

    
}
