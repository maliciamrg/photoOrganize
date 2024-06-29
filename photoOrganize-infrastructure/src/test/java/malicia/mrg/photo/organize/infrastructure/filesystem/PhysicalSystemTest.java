package malicia.mrg.photo.organize.infrastructure.filesystem;

import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class PhysicalSystemTest {

    @Test
    void listFilesErr() {
        //        Given
        IPhysicalSystem physicalSystem = new PhysicalSystem();
        ArrayList<String> allowedExtensions = new ArrayList<>();
        allowedExtensions.add("err");
        ArrayList<String> excludeSubdirectoryRejet = new ArrayList<>();
        excludeSubdirectoryRejet.add("aexclu");


        //        When
        Collection<String> ret = physicalSystem.listFiles(
                "src/test/ressources/testOne",
                allowedExtensions,
                excludeSubdirectoryRejet);

        //        Then
        assertThat(ret).isEqualTo(
                new ArrayList<String>(
                        Arrays.asList(
                                "src/test/ressources/testOne/file1 - Copy (3).err"
                        )
                )
        );
    }

    @Test
    void listFilestxt() {
        //        Given
        IPhysicalSystem physicalSystem = new PhysicalSystem();
        ArrayList<String> allowedExtensions = new ArrayList<>();
        allowedExtensions.add("txt");
        ArrayList<String> excludeSubdirectoryRejet = new ArrayList<>();
        excludeSubdirectoryRejet.add("aexclu");


        //        When
        Collection<String> ret = physicalSystem.listFiles(
            "src/test/ressources/testOne",
            allowedExtensions,
            excludeSubdirectoryRejet);

        //        Then
        assertThat(ret).isEqualTo(
            new ArrayList<String>(
                Arrays.asList(
                    "src/test/ressources/testOne/New folder/file1.txt",
                    "src/test/ressources/testOne/file1 - Copy (2).txt",
                    "src/test/ressources/testOne/file1 - Copy (4).txt",
                    "src/test/ressources/testOne/file1 - Copy.txt",
                    "src/test/ressources/testOne/file1.txt"
                )
            )
        );
    }
    @Test
    void listFilesAll() {
        //        Given
        IPhysicalSystem physicalSystem = new PhysicalSystem();
        ArrayList<String> allowedExtensions = new ArrayList<>();
        allowedExtensions.add("err");
        allowedExtensions.add("txt");
        ArrayList<String> excludeSubdirectoryRejet = new ArrayList<>();


        //        When
        Collection<String> ret = physicalSystem.listFiles(
                "src/test/ressources/testOne",
                allowedExtensions,
                excludeSubdirectoryRejet);

        //        Then
        assertThat(ret).isEqualTo(
            new ArrayList<String>(
                Arrays.asList(
                    "src/test/ressources/testOne/New folder/file1.txt",
                    "src/test/ressources/testOne/aexclu/file1 - Copy (2).txt",
                    "src/test/ressources/testOne/aexclu/file1 - Copy (3).err",
                    "src/test/ressources/testOne/file1 - Copy (2).txt",
                    "src/test/ressources/testOne/file1 - Copy (3).err",
                    "src/test/ressources/testOne/file1 - Copy (4).txt",
                    "src/test/ressources/testOne/file1 - Copy.txt",
                    "src/test/ressources/testOne/file1.txt"
                )
            )
        );
    }
}