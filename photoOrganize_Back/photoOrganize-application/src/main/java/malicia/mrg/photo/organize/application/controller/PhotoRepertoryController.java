package malicia.mrg.photo.organize.application.controller;

import malicia.mrg.photo.organize.domain.api.IPhotoController;
import malicia.mrg.photo.organize.domain.dto.ElementPhotoFolder;
import malicia.mrg.photo.organize.domain.dto.ElementRootFolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/photo")
public class PhotoRepertoryController {
    private final IPhotoController photoController;

    public PhotoRepertoryController(IPhotoController photoController) {
        this.photoController = photoController;
    }

    @GetMapping("/repertories")
    public List<ElementRootFolder> getRepertories() {
        return photoController.getArrayRepertoirePhotoRepertoire();
    }

    @GetMapping("/repertory/{nomUnique}/folders")
    public List<String> getSubRepertories(@PathVariable("nomUnique") String nomUnique) {
        return photoController.getSubDirectories(nomUnique);
    }

    @GetMapping("/repertory/{nomUnique}/folder/{folderName}")
    public ElementPhotoFolder getSubRepertory(@PathVariable("nomUnique") String nomUnique , @PathVariable("folderName") String folderId) {
        return photoController.getSubDirectory(nomUnique,folderId);
    }
}