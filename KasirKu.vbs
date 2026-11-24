Set objShell = CreateObject("WScript.Shell")
Set objFSO = CreateObject("Scripting.FileSystemObject")

' Dapatkan direktori script
strScriptPath = objFSO.GetParentFolderName(WScript.ScriptFullName)

' Cek apakah JAR file ada
strJarPath = strScriptPath & "\KasirKu.jar"
If Not objFSO.FileExists(strJarPath) Then
    ' Jika JAR tidak ada, coba jalankan dari out/
    strCommand = "cmd /c cd /d """ & strScriptPath & """ && java -cp out kasir.Main"
    objShell.Run strCommand, 0, False
Else
    ' Jika JAR ada, jalankan JAR
    strCommand = "javaw -jar """ & strJarPath & """"
    objShell.CurrentDirectory = strScriptPath
    objShell.Run strCommand, 0, False
End If

Set objShell = Nothing
Set objFSO = Nothing
