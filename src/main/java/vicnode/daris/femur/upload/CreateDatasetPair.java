package vicnode.daris.femur.upload;

import java.io.File;
import java.io.FileFilter;

import vicnode.daris.femur.upload.ArcUtil.ArcType;

public class CreateDatasetPair {

    public static void main(String[] args) throws Throwable {

        int specimenNo = Integer.parseInt(args[0]);
        File dir = new File(args[1]);
        File primaryDir = dir;
        String studyName = "Spring8 Sept 2008";
        String[] studyTags = new String[] { "Clinical CT", "microCT" };
        String[] datasetTags = new String[] { "Clinical CT", "microCT" };
        String datasetImageType = "microCT";
        ArcType arcType = ArcType.aar;
        String primaryDatasetCid = CreateStudyAndDatasets.uploadPrimaryDataset(
                dir, false, specimenNo, "2", studyName, studyTags,
                "Spring8 Raw - " + primaryDir.getName().toLowerCase(),
                "Spring8 raw in hipic format", "hipic/series", arcType,
                datasetTags, datasetImageType);
        File[] recDirs = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory()
                        && (f.getName().indexOf("Reconstructed") >= 0
                                || f.getName().indexOf("reconstructed") >= 0);
            }
        });
        for (File recDir : recDirs) {
            String datasetName = "Spring8 Reconstructed" + " - "
                    + dir.getName().toLowerCase();
            CreateStudyAndDatasets.uploadDerivedDataset(recDir, true,
                    specimenNo, "2", studyName, studyTags, datasetName,
                    datasetName + " in tiff format.", "tiff/series", arcType,
                    datasetTags, datasetImageType, primaryDatasetCid);
        }
    }

}
