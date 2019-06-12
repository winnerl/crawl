package com.fun.crawl.task;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.*;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;
import com.fun.crawl.service.NoteBookService;
import com.fun.crawl.service.NoteSysbookService;
import com.fun.crawl.service.NoteUserService;
import com.fun.crawl.utils.dto.yinxiang.LinkNoteListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class YingXiangScheduled {

    public static final String AUTH_TOKEN_OLD = "S=s6:U=f56ff9:E=16b6ea0c9e3:C=16b4a944358:P=1cd:A=en-devtoken:V=2:H=e0989d4623855d826f0b446c3c1b795f";

    public static final String AUTH_TOKEN = "S=s53:U=150a708:E=16b633b594a:C=16b3f2ed410:P=1cd:A=en-devtoken:V=2:H=ae47b61694e4892fa86b5d14692f2d1e";

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(100);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(2, 2, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);


    @Autowired
    private NoteUserService noteUserService;
    @Autowired
    private NoteSysbookService noteSysbookService;
    @Autowired
    private NoteBookService noteBookService;


    public static void main(String[] args) {
        List<LinkedNotebook> linkedNotebooks = listLinkNotebook(AUTH_TOKEN_OLD);
        for (LinkedNotebook linkedNotebook : linkedNotebooks) {
            log.info("linkedNotebook 名称：" + linkedNotebook.getShareName());
            if (linkedNotebook.getShareName().equals("区块链研究院2018")){
                copyLinkNoteBooksToNew(linkedNotebook, AUTH_TOKEN_OLD, AUTH_TOKEN);
            }


        }


    }

    /**
     * 复制笔记本中的内容到新的账号
     *
     * @return
     */
    public static Boolean copyLinkNoteBooksToNew(LinkedNotebook linkedNotebook, String linkToken, String newToken) {
        String bookName = linkedNotebook.getShareName();
        Notebook notebook = new Notebook();
        notebook.setName(bookName);
        NoteStoreClient noteStore = getNoteStore(newToken);
        try {
            try {
                log.info("开始创建笔记本：" + bookName);
                notebook = noteStore.createNotebook(notebook);

            } catch (EDAMUserException e) {
                System.err.println("Error: " + e.getErrorCode().toString()
                        + " parameter: " + e.getParameter());
                String string = e.getErrorCode().toString();
                log.info("笔记本已经存在：" + bookName + "所有笔记本中查找匹配.......");
                List<Notebook> notebooks = null;
                try {
                    notebooks = noteStore.listNotebooks();
                    Optional<Notebook> first = notebooks.stream().filter(book -> book.getName().indexOf(bookName) != -1).findFirst();
                    boolean present = first.isPresent();
                    if (present) {
                        notebook = first.get();
                        log.info("笔记本已经存在：" + bookName + "上次更新时间..."+ DateUtil.format(new Date(notebook.getServiceUpdated()), DatePattern.NORM_DATETIME_PATTERN));
                    }
                } catch (EDAMUserException e1) {
                    e1.printStackTrace();
                } catch (EDAMSystemException e1) {
                    e1.printStackTrace();
                } catch (TException e1) {
                    e1.printStackTrace();
                }
            } catch (EDAMSystemException e) {
                e.printStackTrace();
            } catch (TException e) {
                e.printStackTrace();
            }

            String tonoteBookgUid = notebook.getGuid();
            NoteFilter noteFilter = new NoteFilter();
            noteFilter.setOrder(NoteSortOrder.CREATED.getValue());//根据创建时间排序
            noteFilter.setNotebookGuid(tonoteBookgUid);
            NoteList newNotes = null;
            newNotes = noteStore.findNotes(noteFilter, 0, 1000);
            List<Note> notesNew = newNotes.getNotes();
            LinkNoteListDto dto = listLinkNote(linkedNotebook, linkToken);
            if (dto == null) {
                return false;
            }
            NoteStore.Client client = dto.getClient();
            String authenticationToken = dto.getAuthenticationToken();
            NoteList noteList = dto.getNoteList();
            List<Note> notes = noteList.getNotes();
            NoteAttributes noteAttributes = new NoteAttributes();
            noteAttributes.setAuthor("知识汇聚团队");
            noteAttributes.setSourceURL("kanlem.com");
            for (final Note note : notes) {
//                executorService.execute(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });

                if (notesNew != null && notesNew.size() > 0) {
                    Optional<Note> first = notesNew.stream().filter(thisNote -> thisNote.getTitle().indexOf(note.getTitle()) != -1).findFirst();
                    boolean present = first.isPresent();
                    if (present) {
                        Note sysnote = first.get();
                        //获取数据更新了，需要重新同步数据的笔记
                        log.info("笔记本：" + linkedNotebook.getShareName() + "标题：" + sysnote.getTitle() + "------sysnote更新时间：" + sysnote.getUpdated() + "------note更新时间：" + note.getUpdated());
                        if (sysnote.getUpdated() < note.getUpdated()) {

                            Note copy = client.getNote(authenticationToken, note.getGuid(), true, true, true, true);
                            sysnote.setResources(copy.getResources());
                            sysnote.setTitle(copy.getTitle());
                            String content = copy.getContent();
                            content= content.replace("杨波团队", "知识汇聚团队");
                            content= content.replace("dedao777", "know-who Website：kanlem.com");
                            sysnote.setContent(content);
                            sysnote.setUpdated(note.getUpdated());
                            noteStore.updateNote(sysnote);

                        }
                    } else {
                        log.info("笔记本：" + linkedNotebook.getShareName() + "******创建笔记*********标题：" + note.getTitle());
                        Note copy = client.getNote(authenticationToken, note.getGuid(), true, true, true, true);
                        copy.setAttributes(noteAttributes);
                        copy.setNotebookGuid(tonoteBookgUid);
                        noteStore.createNote(copy);
                    }

                } else {
                    log.info("笔记本：" + linkedNotebook.getShareName() + "******创建笔记*********标题：" + note.getTitle());
                    Note copy = client.getNote(authenticationToken, note.getGuid(), true, true, true, true);
                    copy.setAttributes(noteAttributes);
                    copy.setNotebookGuid(tonoteBookgUid);
                    noteStore.createNote(copy);
                }


                //java8 方式
//                executorService.execute(() -> {
//
//                    try {
//                        if (notesNew != null && notesNew.size() > 0) {
//                            Optional<Note> first = notesNew.stream().filter(thisNote -> thisNote.getTitle().indexOf(note.getTitle()) != -1).findFirst();
//                            boolean present = first.isPresent();
//                            if (present) {
//                                Note sysnote = first.get();
//                                //获取数据更新了，需要重新同步数据的笔记
//                                log.info("笔记本：" + linkedNotebook.getShareName() + "标题：" + sysnote.getTitle() + "------sysnote更新时间：" + sysnote.getUpdated() + "------note更新时间：" + note.getUpdated());
//                                if (sysnote.getUpdated() < note.getUpdated()) {
//                                    Note copy = client.getNote(authenticationToken, note.getGuid(), true, true, true, true);
//                                    sysnote.setResources(copy.getResources());
//                                    sysnote.setContent(copy.getContent());
//                                    sysnote.setTitle(copy.getTitle());
//                                    noteStore.updateNote(sysnote);
//                                }
//                            } else {
//                                log.info("笔记本：" + linkedNotebook.getShareName() + "*****开始创建笔记*********标题：" + note.getTitle());
//                                Note copy = client.getNote(authenticationToken, note.getGuid(), true, true, true, true);
//                                copy.setAttributes(noteAttributes);
//                                copy.setNotebookGuid(tonoteBookgUid);
//                                noteStore.createNote(copy);
//                            }
//
//                        } else {
//                            log.info("笔记本：" + linkedNotebook.getShareName() + "*****开始创建笔记*********标题：" + note.getTitle());
//                            Note copy = client.getNote(authenticationToken, note.getGuid(), true, true, true, true);
//                            copy.setAttributes(noteAttributes);
//                            copy.setNotebookGuid(tonoteBookgUid);
//                            noteStore.createNote(copy);
//                        }
//                    } catch (EDAMUserException e) {
//                        e.printStackTrace();
//                    } catch (EDAMSystemException e) {
//                        e.printStackTrace();
//                    } catch (EDAMNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (TException e) {
//                        e.printStackTrace();
//                    }
//                });
            }

        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 带有数据-------笔记
     * 复制连接Link笔记本中的某一条数据...
     *
     * @param token
     * @param note
     * @return
     */
    public Note copyLinkBook(Note note, NoteStore.Client client, String authenticationToken) {
        try {
            Note clientNote = client.getNote(authenticationToken, note.getGuid(), true, true, true, true);
            return clientNote;
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取所有分享的链接笔记本中的笔记
     *
     * @return
     * @throws Exception
     */
    public static LinkNoteListDto listLinkNote(LinkedNotebook linkedNotebook, String token) {
        log.info("*****linkedNotebook******笔记本名称：" + linkedNotebook.getShareName());
        String shareKey = linkedNotebook.getShareKey();
        THttpClient tHttpClient = null;
        try {
            tHttpClient = new THttpClient(linkedNotebook.getNoteStoreUrl());
            TBinaryProtocol tBinaryProtocol = new TBinaryProtocol(tHttpClient);
            NoteStore.Client client = new NoteStore.Client(tBinaryProtocol);
            AuthenticationResult authenticationResult = client.authenticateToSharedNotebook(shareKey, token);
            String authenticationToken = authenticationResult.getAuthenticationToken();
            SharedNotebook sharedNotebookByAuth = client.getSharedNotebookByAuth(authenticationToken);
            NoteFilter noteFilter = new NoteFilter();
            noteFilter.setOrder(NoteSortOrder.CREATED.getValue());//根据创建时间排序
            noteFilter.setNotebookGuid(sharedNotebookByAuth.getNotebookGuid());
            NoteList notes = client.findNotes(authenticationToken, noteFilter, 0, 1000);
            LinkNoteListDto dto = new LinkNoteListDto();
            dto.setClient(client);
            dto.setAuthenticationToken(authenticationToken);
            dto.setShareKey(shareKey);
            dto.setNoteList(notes);
            return dto;
        } catch (EDAMUserException | EDAMNotFoundException | EDAMSystemException | TException e) {
            log.error("*****linkedNotebook******笔记本名称：" + linkedNotebook.getShareName(), e);
            NoteStoreClient noteStore = getNoteStore(token);
            int i = 0;
            try {
                i = noteStore.expungeLinkedNotebook(linkedNotebook.getGuid());
            } catch (EDAMUserException e1) {
                e1.printStackTrace();
            } catch (EDAMNotFoundException e1) {
                e1.printStackTrace();
            } catch (EDAMSystemException e1) {
                e1.printStackTrace();
            } catch (TException e1) {
                e1.printStackTrace();
            }
            log.error("****删除*****linkedNotebook******笔记本名称：" + linkedNotebook.getShareName() + "状态：" + i);
        }
        return null;
    }


    /**
     * 获取所有分享的链接笔记本
     *
     * @return
     * @throws Exception
     */
    public static List<LinkedNotebook> listLinkNotebook(String token) {
        try {
            List<LinkedNotebook> linkedNotebooks = getNoteStore(token).listLinkedNotebooks();
            return linkedNotebooks;
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取所有的笔记本
     *
     * @return
     * @throws Exception
     */
    public List<Notebook> listNotebook(String token) {
        List<Notebook> notebooks = null;
        try {
            notebooks = getNoteStore(token).listNotebooks();
            return notebooks;
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取操作笔记Client
     *
     * @param auth_token
     * @return
     */
    public static NoteStoreClient getNoteStore(String auth_token) {
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.YINXIANG, auth_token);
        ClientFactory factory = new ClientFactory(evernoteAuth);
        try {
            NoteStoreClient noteStore = factory.createNoteStoreClient();
            return noteStore;
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取用户Client
     *
     * @param auth_token
     * @return
     */
    public static UserStoreClient getUserStore(String auth_token) {
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.YINXIANG, auth_token);
        ClientFactory factory = new ClientFactory(evernoteAuth);
        UserStoreClient userStoreClient = null;
        try {
            userStoreClient = factory.createUserStoreClient();
            return userStoreClient;
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        return null;
    }


}

