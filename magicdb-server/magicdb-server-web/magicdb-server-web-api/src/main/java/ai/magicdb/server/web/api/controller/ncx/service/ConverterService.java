package ai.magicdb.server.web.api.controller.ncx.service;

import ai.magicdb.server.web.api.controller.ncx.vo.UploadVO;

import java.io.File;
import java.io.InputStream;

/**
 * ConverterService
 *
 * @author lzy
 **/
public interface ConverterService {

    UploadVO uploadFile(File file);

    UploadVO dbpUploadFile(File file);

    UploadVO datagripUploadFile(String text);
}
