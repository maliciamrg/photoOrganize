package malicia.mrg.photo.organize.application.controller;

import malicia.mrg.photo.organize.domain.api.IPhotoController;
import malicia.mrg.photo.organize.domain.dto.ElementPhotoFolder;
import malicia.mrg.photo.organize.domain.dto.ElementRootFolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/photo")
public class PhotoRepertoryController {
    private final IPhotoController photoController;

    public PhotoRepertoryController(IPhotoController photoController) {
        this.photoController = photoController;
    }

    @GetMapping("/repertories")
    public List<Map<String,String>> getRepertories() {
        return photoController.getArrayRepertoirePhotoRepertoire();
    }

    @GetMapping("/repertory/{rootFolderId}")
    public ElementRootFolder getRepertory(@PathVariable("rootFolderId") Integer rootFolderId) {
        return photoController.getArrayRepertoirePhotoRepertoire(rootFolderId);
    }

    @GetMapping("/repertory/{rootFolderId}/folders")
    public List<String> getSubRepertories(@PathVariable("rootFolderId") Integer rootFolderId) {
        return photoController.getSubDirectories(rootFolderId);
    }

    @GetMapping("/repertory/{rootFolderId}/folder/{folderId}")
    public ElementPhotoFolder getSubRepertory(@PathVariable("rootFolderId") Integer rootFolderId , @PathVariable("folderId") Integer folderId) {
        return photoController.getSubDirectory(rootFolderId,folderId);
    }
}