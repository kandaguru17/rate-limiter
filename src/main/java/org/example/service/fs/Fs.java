package org.example.service.fs;

import java.util.*;

public class Fs {

    final Dir root;

    static class Dir {
        String name;
        Map<String, Dir> children = new HashMap<>();
        Map<String, File> files = new HashMap<>();
        int size = 0;

        Dir(String n) {
            name = n;
        }

        @Override
        public String toString() {
            return "Dir{" +
                    "name='" + name + '\'' +
                    ", children=" + children +
                    ", files=" + files +
                    ", size=" + size +
                    '}';
        }
    }


    Map<String,Collection> collections = new HashMap<>();

    static class Collection{
        String name;
        int size;
        Set<File> files = new HashSet<>();

        void addFile(File fileName){
            files.add(fileName);
            size+= fileName.size;
        }
    }

    static class File {
        String name;
        int size;
        Set<Collection> collections = new HashSet<>();

        File(String name, int size) {
            this.name = name;
            this.size = size;
        }

        public void addToCollection(Collection collection){
            collections.add(collection);
        }

        @Override
        public String toString() {
            return "File{" +
                    "name='" + name + '\'' +
                    ", size=" + size +
                    '}';
        }
    }

    public Fs() {
        root = new Dir("/");
    }

    public void addToCollection(String fileName,String collectionName){
      collections.get(collectionName);
      // create new collection if no collection

        // find the file

        // add file to collection

        // associate collection to file
    }

    public void addDir(String dirName) {
        String[] directories = dirName.split("/");
        Dir cur = findParent(root, directories, 1);
        if (cur == null) throw new RuntimeException("No parent punda");
        if (cur.children.containsKey(dirName)) return;
        Dir newDir = new Dir(directories[directories.length - 1]);
        cur.children.put(directories[directories.length - 1], newDir);
    }

    public void addFile(String filePath, int size) {
        String[] directories = filePath.split("/");
        Dir parent = findParent(root, directories, 1);
        if (parent == null) throw new RuntimeException("No parent punda");
        File file = new File(directories[directories.length - 1], size);
        parent.files.put(directories[directories.length - 1], file);
    }

    public void addFileV2(String fileName, int size) {
        String[] dir = fileName.split("/");
        addFileV2(dir, 1, dir[dir.length - 1], size, root);
    }

    private void addFileV2(String[] dir, int i, String file, int size, Dir cur) {
        if (i == dir.length - 1){
            File file1 = new File(file, size);
            cur.files.put(file, file1);
            cur.size += size;
            return;
        }
        if (cur.children.containsKey(dir[i])) {
            addFileV2(dir, i + 1, file, size, cur.children.get(dir[i]));
        } else {
            throw new RuntimeException("Dir ill da punda");
        }
        cur.size+=size;
    }

    public List<File> getTopNFiles(String dirName, int n) {
        PriorityQueue<File> maxHeap = new PriorityQueue<>((o1, o2) -> o2.size - o1.size);
        String[] dir = dirName.split("/");
        Dir foundDir;
        if (dir.length == 0) {
            foundDir = root;
        } else {
            foundDir = find(root, dir, 1);
        }
        walkFiles(foundDir, maxHeap);
        List<File> files = new LinkedList<>();
        while (n > 0 && !maxHeap.isEmpty()) {
            files.add(maxHeap.poll());
            n--;
        }
        return files;
    }

    private void walkFiles(Dir cur, PriorityQueue<File> pq) {
        if (cur == null) return;
        pq.addAll(cur.files.values());
        for (var dir : cur.children.entrySet()) {
            walkFiles(dir.getValue(), pq);
        }
    }

    private Dir find(Dir root, String[] directories, int i) {
        if (i == directories.length) return root;
        if (root.children.containsKey(directories[i]))
            return find(root.children.get(directories[i]), directories, i + 1);
        return root;
    }

    private Dir findParent(Dir root, String[] directories, int i) {
        if (i == directories.length - 1) return root;
        if (root.children.containsKey(directories[i]))
            return findParent(root.children.get(directories[i]), directories, i + 1);
        return root;
    }

    public boolean exist(String dirName) {
        String[] directories = dirName.split("/");
        Dir parent = findParent(root, directories, 1);
        if (parent == null) return false;

        if (directories[directories.length - 1].equals("*") && !parent.children.isEmpty())
            return true;

        return parent.children.containsKey(directories[directories.length - 1])
                || parent.files.containsKey(directories[directories.length - 1]);
    }
}
