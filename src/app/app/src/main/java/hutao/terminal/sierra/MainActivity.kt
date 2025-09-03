package hutao.terminal.sierra
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        val commandPrompt = findViewById<TextInputEditText>(R.id.commandToRun);
        val txt = findViewById<TextView>(R.id.showTerminalOutput);
        var lastExitCode: Int? = null;
        commandPrompt.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                val command = commandPrompt.text?.toString()?.trim();
                if(command == "lastReturnCode") {
                    runOnUiThread {
                        txt.append("${lastExitCode ?: "No previous command"}\n");
                    }
                    return@setOnEditorActionListener true
                }
                else if(command == "clear" || command == "cls") {
                    txt.text = "";
                    return@setOnEditorActionListener true
                }
                else if(command == "exit") exitProcess(1);
                runOnUiThread {
                    val proc = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
                    val stdout = proc.inputStream.bufferedReader().use { it.readText() }
                    val stderr = proc.errorStream.bufferedReader().use { it.readText() }
                    proc.waitFor();
                    lastExitCode = proc.exitValue();
                    if(stdout.isNotBlank()) txt.append(stdout.trim() + "\n")
                    if(stderr.isNotBlank()) txt.append("${stderr.trim()}\n")
                }
                true
            }
            else false
        }
    }
}
