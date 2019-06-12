package com.fun.crawl.utils.dto.yinxiang;

import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.notestore.NoteStore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LinkNoteListDto {
    private String authenticationToken;
    private String shareKey;
    private NoteStore.Client client;
    private NoteList noteList;

}
