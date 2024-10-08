package malicia.mrg.photo.organize.domain.dto;

import malicia.mrg.photo.organize.domain.spi.ILogicalSystem;
import malicia.mrg.photo.organize.domain.spi.IParams;
import malicia.mrg.photo.organize.domain.spi.IPhysicalSystem;
import malicia.mrg.photo.organize.domain.spi.stub.LogicalSystemStub;
import malicia.mrg.photo.organize.domain.spi.stub.ParamsStub;
import malicia.mrg.photo.organize.domain.spi.stub.PhysicalSystemStub;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ElementPhotoFolderTest {
    @Test
    void testCreateElementPhotoFolder() {
        //        Given
        ILogicalSystem logicalSystem = new LogicalSystemStub();
        IPhysicalSystem physicalSystem = new PhysicalSystemStub();
        IParams params= new ParamsStub();;
        ElementRootFolder elementRootFolder = params.getArrayRepertoirePhotoNmUnique(0);
        List<String> valeurAdmise = new ArrayList<>();
        valeurAdmise.add("£DATE£");
        valeurAdmise.add("@00_EVENT@");
        valeurAdmise.add("@00_WHERE@");
        valeurAdmise.add("@00_WHAT@|@00_WHO@");
        elementRootFolder.setZoneValeurAdmise(valeurAdmise);
        ElementPhotoFolder elementPhotoFolder = new ElementPhotoFolder(
                "testdirectory",
                elementRootFolder,
                physicalSystem,
                logicalSystem);

//        When
        String ret = elementPhotoFolder.toString();

//        Then
        assertThat(ret).contains("isFolderNameValide=false");
    }
    @Test
    void testFolderNameValid() {
        //        Given
        ILogicalSystem logicalSystem = new LogicalSystemStub();
        IPhysicalSystem physicalSystem = new PhysicalSystemStub();
        IParams params= new ParamsStub();;
        ElementRootFolder elementRootFolder = params.getArrayRepertoirePhotoNmUnique(0);
        List<String> valeurAdmise = new ArrayList<>();
        valeurAdmise.add("£DATE£");
        valeurAdmise.add("@00_EVENT@");
        valeurAdmise.add("@00_WHERE@");
        valeurAdmise.add("@00_WHAT@|@00_WHO@");
        elementRootFolder.setZoneValeurAdmise(valeurAdmise);

        SimpleDateFormat repDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String datePart = repDateFormat.format(new Date());
        ElementPhotoFolder elementPhotoFolder = new ElementPhotoFolder(
                datePart + "_eventKey_whereKey_whoKey",
                elementRootFolder,
                physicalSystem,
                logicalSystem);

//        When
        String ret = elementPhotoFolder.toString();

//        Then
        assertThat(ret).contains("isFolderNameValide=true");
    }
    @Test
    void testFolderNameNonValid() {
        //        Given
        ILogicalSystem logicalSystem = new LogicalSystemStub();
        IPhysicalSystem physicalSystem = new PhysicalSystemStub();
        IParams params= new ParamsStub();;
        ElementRootFolder elementRootFolder = params.getArrayRepertoirePhotoNmUnique(0);
        List<String> valeurAdmise = new ArrayList<>();
        valeurAdmise.add("£DATE£");
        valeurAdmise.add("@00_EVENT@");
        valeurAdmise.add("@00_WHERE@");
        valeurAdmise.add("@00_WHAT@|@00_WHO@");
        elementRootFolder.setZoneValeurAdmise(valeurAdmise);

        SimpleDateFormat repDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String datePart = repDateFormat.format(new Date());
        ElementPhotoFolder elementPhotoFolder = new ElementPhotoFolder(
                datePart + "_eventKey_whereKey_whoOOOKey",
                elementRootFolder,
                physicalSystem,
                logicalSystem);

//        When
        String ret = elementPhotoFolder.toString();

//        Then
        assertThat(ret).contains("@00_WHAT@|@00_WHO@").contains("isFolderNameValide=false");
    }
}