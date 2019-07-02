package com.webank.ai.fate.board.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.webank.ai.fate.board.global.ErrorCode;
import com.webank.ai.fate.board.global.ResponseResult;
import com.webank.ai.fate.board.log.LogFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 **/
@Controller
public class LogController {
    private final Logger logger = LoggerFactory.getLogger(LogController.class);
    @Autowired
    LogFileService logFileService;



    @RequestMapping(value = "/queryLogWithSizeSSH/{jobId}/{componentId}/{type}/{begin}/{end}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult queryLogWithSizeSSH(@PathVariable String componentId, @PathVariable String jobId,
                                              @PathVariable Integer begin, @PathVariable String type, @PathVariable Integer end) throws Exception {
        logger.info("parameters for " + "componentId:" + componentId + ", jobId:" + jobId + ", begin;" + begin + ", end:" + end + "type");

        String filePath = logFileService.buildFilePath(jobId, componentId, type);

        Preconditions.checkArgument(filePath != null && !filePath.equals(""));

        String ip = logFileService.getJobTaskInfo(jobId, componentId).ip;

        Preconditions.checkArgument(ip != null && !ip.equals(""));

        List<Map> logs = logFileService.getRemoteLogWithFixSize(jobId, componentId, type, begin, end - begin + 1);

        ResponseResult result = new ResponseResult();

        result.setData(logs);

        return result;

    }


    @RequestMapping(value = "/queryLogWithSize/{jobId}/{componentId}/{type}/{begin}/{end}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult queryLogWithSize(@PathVariable String componentId, @PathVariable String jobId,
                                           @PathVariable Integer begin, @PathVariable String type, @PathVariable Integer end) {

        logger.info("parameters for " + "componentId:" + componentId + ", jobId:" + jobId + ", begin;" + begin + ", end:" + end);

        String filePath = logFileService.buildFilePath(jobId, componentId, type);

        Preconditions.checkArgument(filePath != null && !filePath.equals(""));

        logger.info("path for logfile：" + filePath);


        RandomAccessFile file = null;
        List<Map> result = Lists.newArrayList();

        if (begin > end || begin <= 0) {
            return new ResponseResult<>(ErrorCode.PARAM_ERROR, "Error for incoming parameters!");
        }
        try {
            int lineCount = 0;
            int byteSize = 0;

            file = new RandomAccessFile(filePath, "r");

            String line = null;
            do {
                line = file.readLine();
                if (line != null) {
                    lineCount++;
                    byteSize += line.getBytes().length;
                    if (lineCount >= begin) {
                        Map logMap = LogFileService.toLogMap(line, lineCount);
                        result.add(logMap);
                        logger.info("wrapped log information：" + logMap);
                    }
                }
            }
            while (line != null && lineCount < end);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception for building file stream!", e);
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("Exception for closing file stream!", e);
                }
            }
        }
        return new ResponseResult<>(ErrorCode.SUCCESS, result);
    }

//    private String getFilePath(String j) {
//
//        LogFileUtil.buildFilePath()
//
//        return "/data/projects/fate/serving-server/logs/console.log";
////            return "/Users/kaideng/work/webank/code/fate-board/logs/borad/2019-05-30/info.0.log";
////        return "E:\\workspace\\console.log";
//    }
}