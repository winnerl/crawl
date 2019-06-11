package com.fun.crawl.task;


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
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.SharedNotebook;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;
import com.fun.crawl.service.NoteBookService;
import com.fun.crawl.service.NoteSysbookService;
import com.fun.crawl.service.NoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class YingXiangScheduled {

    public static final String AUTH_TOKEN = "S=s53:U=150a708:E=16b633b594a:C=16b3f2ed410:P=1cd:A=en-devtoken:V=2:H=ae47b61694e4892fa86b5d14692f2d1e";


    public static final String AUTH_TOKEN_NEW = "S=s53:U=150a708:E=16b633b594a:C=16b3f2ed410:P=1cd:A=en-devtoken:V=2:H=ae47b61694e4892fa86b5d14692f2d1e";


    @Autowired
    private NoteUserService noteUserService;
    @Autowired
    private NoteSysbookService noteSysbookService;
    @Autowired
    private NoteBookService noteBookService;

    /**
     * 获取所有分享的链接笔记本
     *
     * @return
     * @throws Exception
     */
    public List<LinkedNotebook> listLinkNotebook(String token) {

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
     * 获取所有分享的链接笔记本
     *
     * @return
     * @throws Exception
     */
    public NoteList listLinkNote(String token, LinkedNotebook linkedNotebook) {
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
            return notes;
        } catch (EDAMUserException | EDAMNotFoundException | EDAMSystemException | TException e) {
            log.error("*****linkedNotebook******笔记本名称：" + linkedNotebook.getShareName(), e);
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

