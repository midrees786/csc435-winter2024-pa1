import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

 class CleanDataset {


    private static long datasetSize = 0;
    private double executionTime = 0.0;

    public CleanDataset(Path inputdir, Path outdir) throws IOException {
        long startTime = System.currentTimeMillis();
        getDirectories(inputdir, inputdir, outdir);
        long endTime = System.currentTimeMillis();

        executionTime = (endTime - startTime);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("improper number of arguments");
            System.exit(1);
        }

        Path inputdir = Paths.get(args[0]);
        Path outdir = Paths.get(args[1]);

        try {
            CleanDataset cleaner = new CleanDataset(inputdir, outdir);
            cleaner.printResults();
        } catch (IOException e) {
            System.err.println("Error cleaning dataset: " + e.getMessage());
            System.exit(1);
        }
    }



    private static void getDirectories(Path currentPath, Path inputdir, Path outputdir) throws IOException {
        File[] files = currentPath.toFile().listFiles();
        if(files!=null){
            for(File file: files){
                if(file.isDirectory()){
                    getDirectories(file.toPath(), inputdir, outputdir);;
                }
                else if(file.isFile() && file.getName().endsWith(".txt")){
                    readFile(file.toPath(), inputdir, outputdir);
                }
            }
        }
    }

    private static void readFile(Path file, Path inputdir, Path outdir ) throws IOException {
        String content= new String(Files.readAllBytes(file));
        String cleanedData = cleanData(content);

        Path relative = inputdir.relativize(file);
        Path output = outdir.resolve(relative);
        String outPutCleanedPath = output.toString().replace("Dataset", "CleanDataset");
        output = Paths.get(outPutCleanedPath);
       // Create file Directories
        Files.createDirectories(output.getParent());
        Files.write(output,cleanedData.getBytes());

        datasetSize += file.toFile().length();
    }

    public void printResults() {
        System.out.println("Finished cleaning " + datasetSize / (1024.0 * 1024.0) + " MiB of data"
                + " in " + executionTime + " millisecond");
    }
    public static String cleanData(String content){
        content = content.replace("\r","");
        content = content.replaceAll("\\n{2,}","\n");
        content = content.replaceAll("[();*\\[\\]:,./#-]", "");
        return content;
    }

}
