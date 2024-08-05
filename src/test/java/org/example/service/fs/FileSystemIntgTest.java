package org.example.service.fs;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemIntgTest {

    Fs fileSystem = new Fs();

    @Test
    void shouldInitializeFileSystem() {
        assertNotNull(fileSystem);
    }

    @Test
    void shouldCreateDirInFs() {
        fileSystem.addDir("/newDir");
        assertTrue(fileSystem.exist("/newDir"));
    }

    @Test
    void shouldReturnFalseIfDirAbsent() {
        assertFalse(fileSystem.exist("/non-existent"));
    }

    @Test
    void shouldAddFiles() {
        fileSystem.addDir("/newDir");
        fileSystem.addFile("/newDir/ab.txt", 30);
        assertTrue(fileSystem.exist("/newDir/ab.txt"));
    }


    @Test
    void shouldFindDirWithWildCards() {
        Fs fileSystem = new Fs();
        fileSystem.addDir("/newDir");
        fileSystem.addDir("/newDir/myDir");
        assertTrue(fileSystem.exist("/newDir/*"));
    }

    @Test
    void shouldReturnTopNFiles(){
        Fs fileSystem = new Fs();
        fileSystem.addDir("/a");
        fileSystem.addDir("/b");
        fileSystem.addFile("/b/b.txt",30);
        fileSystem.addFile("/a/a.txt",25);
        fileSystem.addFile("/a/a1.txt",35);

        List<Fs.File> topNFiles = fileSystem.getTopNFiles("/", 2);
        assertEquals(2,topNFiles.size());
    }


    @Test
    void shouldAddFileV2(){
        Fs fs = new Fs();
        fs.addDir("/newDir");
        fs.addDir("/newDir/myDir");
        fs.addFileV2("/newDir/ab.txt", 30);
        fs.addFileV2("/newDir/myDir/cd.txt",20);
        fs.addFileV2("/root.txt",10);
        List<Fs.File> topNFiles = fs.getTopNFiles("/", 10);
        System.err.println(topNFiles);
        System.err.println(fs.root.size);
    }

}
