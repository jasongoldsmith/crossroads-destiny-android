//package com.example.sharmha.travellerdestiny.core;
//
///**
// * Created by sharmha on 2/24/16.
// */
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.util.Log;
//
//import com.f80prototype.Constants;
//import com.f80prototype.data.Filenames;
//import com.f80prototype.verizon.protoandroid.core.AppEngine;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//class LogToFile
//{
//    public static final int MAX_FILE_SIZE = 2;
//    private FileWriter fileWriter;
//
//    public LogToFile() throws RuntimeException, IOException
//    {
//        File sdcard = new File( Filenames.SD_CARD );
//        File file1 = null;
//        File file2 = null;
//        File file3 = null;
//        try
//        {
//            file1 = getAttachement(AppEngine.getInstance().getTrimbleContext().getContext(), Filenames.TRIMBLE1 );
//            file2 = getAttachement( AppEngine.getInstance().getTrimbleContext().getContext(), Filenames.TRIMBLE2 );
//            file3 = getAttachement( AppEngine.getInstance().getTrimbleContext().getContext(), Filenames.TRIMBLE3 );
//        }
//        catch( TrimbleException e1 )
//        {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        if( !sdcard.exists() )
//        {
//            throw new RuntimeException( "SD card not exists!" );
//        }
//        else if( file1 == null || file2 == null || file3 == null )
//        {
//            throw new RuntimeException( "file1, file2, or file3 is null" );
//        }
//        else
//        {
//            if( !file1.exists() )
//            {
//                try
//                {
//                    file1.createNewFile();
//                }
//                catch( IOException e )
//                {
//                    throw e;
//                }
//            }
//            else
//            {
//                long fileSize = ( file1.length() >>> 20 );// convert to M bytes
//                if( fileSize > MAX_FILE_SIZE )
//                {
//                    if( !file2.exists() )
//                    {
//                        file1.renameTo( file2 );
//                        file1 = new File( Filenames.TRIMBLE1 );
//                        try
//                        {
//                            file1.createNewFile();
//                        }
//                        catch( IOException e )
//                        {
//                            throw e;
//                        }
//                    }
//                    else
//                    {
//                        file2.renameTo( file3 );
//                        file2 = new File( Filenames.TRIMBLE2 );
//                        file1.renameTo( file2 );
//                        file1 = new File( Filenames.TRIMBLE1 );
//                        try
//                        {
//                            file1.createNewFile();
//                        }
//                        catch( IOException e )
//                        {
//                            throw e;
//                        }
//                    }
//                }
//            }
//            fileWriter = new FileWriter( file1, true );
//        }
//    }
//
//    // we use one space to separate elements
//    @SuppressLint( "SimpleDateFormat" )
//    public void writeLogToFile( int priority, String tag, String message )
//    {
//        Date date = new Date();
//        SimpleDateFormat simpleDateFormate = new SimpleDateFormat( "yyyy:MM:dd kk:mm:ss.SSS" );
//        String strLog = simpleDateFormate.format( date );
//
//        StringBuffer sb = new StringBuffer( strLog );
//        sb.append( ' ' );
//        sb.append( strPriority[ priority ] );
//        sb.append( ' ' );
//        sb.append( tag );
//        sb.append( ' ' );
//        sb.append( message );
//        sb.append( '\n' );
//        strLog = sb.toString();
//
//        try
//        {
//            fileWriter.write( strLog );
//            fileWriter.flush();
//        }
//        catch( IOException e )
//        {
//            Log.e( "LogToFile", "", e );
//        }
//    }
//
//    private static String strPriority[];
//    static
//    {
//        strPriority = new String[8];
//        strPriority[ 0 ] = "";
//        strPriority[ 1 ] = "";
//        strPriority[ 2 ] = "verbose";
//        strPriority[ 3 ] = "debug";
//        strPriority[ 4 ] = "info";
//        strPriority[ 5 ] = "warn";
//        strPriority[ 6 ] = "error";
//        strPriority[ 7 ] = "ASSERT";
//    }
//
//    private File getAttachement( Context ctx, String fileName )
//    {
//        return getOutputFile( ctx, fileName );
//    }
//
//    private File getOutputFile( Context ctx, String fileName )
//    {
//        File outDir = getAttachmentFolder( ctx );
//        if( outDir == null )
//        {
//            return null;
//        }
//
//        if( !outDir.exists() && !outDir.mkdir() )
//        {
//            return null;
//        }
//
//        // create a new file, specifying the path, and the filename
//        // which we want to save the file as.
//        File attachment = new File( outDir, String.format( Constants.ATTACHMENT, fileName ) );
//
//        return attachment;
//
//    }
//
//    private File getAttachmentFolder( Context ctx )
//    {
//        if( ctx == null )
//        {
//            return null;
//        }
//
//        File outDir = null;
//        String sdState = android.os.Environment.getExternalStorageState();
//        if( android.os.Environment.MEDIA_MOUNTED.equals( sdState ) )
//        {
//            outDir = ctx.getExternalFilesDir( Constants.ATTACHMENT_PATH );
//        }
//
//        return outDir;
//    }
//}
