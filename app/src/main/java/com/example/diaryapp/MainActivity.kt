package com.example.diaryapp

import android.content.pm.PackageManager
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import com.example.diaryapp.adapter.ViewPagerCalendarAdapter
import com.example.diaryapp.fragment.FragmentCalendar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.opencsv.CSVReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList
import android.content.Intent




class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var database: Database

    companion object{
        val STORAGE_REQUEST_CODE_EXPORT = 1
        val STORAGE_REQUEST_CODE_IMPORT = 2

        lateinit var storagePermission: Array<String>
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)

        database = Database(this, "DatabaseDiary.sqlite", null, 1)

        //database.QueryData("DROP TABLE DIARY")
        database.QueryData("CREATE TABLE IF NOT EXISTS DIARY(ID INTEGER PRIMARY KEY AUTOINCREMENT, Title VARCHAR(100), Content VARCHAR(1000), Date DateTime)")
        //database.QueryData("INSERT INTO DIARY VALUES(null, 'test2', 'test2', '2021-11-01 00:00:00')")

        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun checkStoragePermission(): Boolean{
        val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        return result
    }
    private fun requestStoragePermissionImport(){
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE_IMPORT)
    }
    private fun requestStoragePermissionExport(){
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE_EXPORT)
    }

    private fun importCSV() {
        var filePathAndName: String = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/SQliteBackupDiary/"+"SQLite_backup.csv"
        var csvFile: File = File(filePathAndName)

        if(csvFile.exists()){
            try {
                var csvReader = CSVReader(FileReader(csvFile.absolutePath))
                database.QueryData("DELETE FROM DIARY");
                var nextLine: Array<String>?
                nextLine = csvReader.readNext()
                while (nextLine != null){
                    var id = nextLine[0]
                    var title = nextLine[1]
                    var content = nextLine[2]
                    var date = nextLine[3]

                    database.QueryData("INSERT INTO DIARY VALUES(null, '"+title+"', '"+content+"', '"+date+"')")
                    nextLine = csvReader.readNext()
                }
//                do {
//                    nextLine = csvReader.readNext()
//                    var id = nextLine[0]
//                    var title = nextLine[1]
//                    var content = nextLine[2]
//                    var date = nextLine[3]
//                    Log.d("TAG", "importCSV: $title , $content, $date")
//
//                    database.QueryData("INSERT INTO DIARY VALUES(null, '"+title+"', '"+content+"', '"+date+"')")
//                    nextLine = csvReader.readNext()
//                }while (nextLine!=null)


                Toast.makeText(this, "Backup Restored...", Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "No backup found....", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportCSV() {
        var folder: File = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/"+"SQliteBackupDiary")

        var isFolderCreated= false
        if(!folder.exists()){
            isFolderCreated = folder.mkdir()
            Log.d("TAG", "isFolderCreated: $isFolderCreated")
        }


        var csvFileName = "SQLite_backup.csv"
        var filePathAndName = folder.toString() + "/" +csvFileName

        var recordList : ArrayList<Diary> = ArrayList()
        recordList.clear()
        var cursor: Cursor = database.GetData("Select * FROM DIARY")
        while (cursor.moveToNext()){
            val id = cursor.getInt(0)
            val title = cursor.getString(1)
            val content = cursor.getString(2)
            val date = cursor.getString(3)
            val dateConvert: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)
            recordList.add(Diary(id,title,content, dateConvert))
        }

        try{
            var fw = FileWriter(filePathAndName)
            for(i in 0..recordList.size-1){
                fw.append(""+ recordList.get(i).id)
                fw.append(",")
                fw.append(""+ recordList.get(i).title)
                fw.append(",")
                fw.append(""+ recordList.get(i).content)
                fw.append(",")
                val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                val strDate = dateFormat.format(recordList.get(i).date)
                fw.append(""+ strDate + " 00:00:00")
                fw.append("\n")
            }
            fw.flush()
            fw.close()

            Toast.makeText(this, "Backup Exported to: "+ filePathAndName, Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            STORAGE_REQUEST_CODE_EXPORT ->{
                if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    exportCSV()
                }else{
                    Toast.makeText(this, "Storage permission required...", Toast.LENGTH_SHORT).show();
                }
            }
            STORAGE_REQUEST_CODE_IMPORT ->{
                if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    importCSV()
                }else{
                    Toast.makeText(this, "Storage permission required...", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_backup, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.backup -> {
                if(checkStoragePermission()){
                    exportCSV()
                }else{
                    requestStoragePermissionExport()
                }
            }
            R.id.restore ->{
                if(checkStoragePermission()){
                    importCSV()
                    val intent = intent
                    finish()
                    startActivity(intent)
                }else{
                    requestStoragePermissionImport()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}