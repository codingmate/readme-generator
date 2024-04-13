import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

fun main() {
    //ReadmeGenerator().cloneRepository("https://github.com/codingmate/leetcode.git")
    val model = "mistral:7b"
    val prompt = "10자로 답변해줘. 오늘 몇 시야?. 10자로 나타내는게 불가능하면 어떻게든 나타내봐. 설명하는 글은 나타내지 않아도 돼"

    ReadmeGenerator().sendApiRequest(model, prompt).let {
        println(it!!.response)
    }



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

    fun searchDirectory(rootDirectory: String): HashMap<File, List<File>> {

        val treeMap = HashMap<File, List<File>>()
        val rootDir = File(rootDirectory)

        fun traverseDirectory(directory: File) {
            val filesAndDirs = directory.listFiles()?.toList() ?: listOf()

            treeMap[directory] = filesAndDirs

            filesAndDirs.filter { it.isDirectory }.forEach {subDirectory ->
                traverseDirectory(subDirectory)
            }
        }
        if ( !rootDir.exists() || !rootDir.isDirectory ) {
            println("Provided root directory does not exist or is not a directory.")
            return treeMap
        }

        traverseDirectory(rootDir)

        return treeMap
    }

    data class ApiResponse(
        val model: String,
        val created_at: String,
        val response: String,
        val done: Boolean,
        val total_duration: Long,
        val load_duration: Long,
        val prompt_eval_duration: Long,
        val eval_count: Int,
        val eval_duration: Long
    )
    fun sendApiRequest(model: String, prompt: String): ApiResponse? {
        val client = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // JSON 데이터 구성
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val jsonData = """
        {
            "model": "$model",
            "prompt": "$prompt",
            "stream": false
        }
    """.trimIndent()

        val body: RequestBody = jsonData.toRequestBody(jsonMediaType)

        // Request 구성
        val request = Request.Builder()
            .url("http://localhost:11434/api/generate")
            .post(body)
            .build()

        // 요청 실행 및 응답 처리
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to send request: ${response.message}")
            } else {
                val responseBody = response.body?.string()
                return Gson().fromJson(responseBody, ApiResponse::class.java)
            }
        }
    }
}
