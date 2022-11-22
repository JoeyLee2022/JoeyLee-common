package com.joeylee.common.config.mq.ibmmq;

import cn.hutool.core.util.ObjectUtil;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.MQSimpleConnectionManager;
import com.ibm.msg.client.wmq.compat.base.internal.MQC;
import com.joeylee.common.domain.enums.ResultCode;
import com.joeylee.common.domain.exception.JoeyLeeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.config.JmsListenerContainerFactory;

import java.util.Hashtable;

/**
 * @author joeylee
 */
public class MqEnvConfig extends MqConfig {

    @Autowired(required = false)
    private MQSimpleConnectionManager mqSimpleConnectionManager;

    /**
     * 定义队列
     *
     * @return
     */
    public MQMessage getMQMessage() {
        // 创建消息
        MQMessage m = new MQMessage();

        // 设置MQMD字段格式
        m.format = MQC.MQFMT_STRING;
        // m.messageId = msgId == null ? MQC.MQMI_NONE : msgId.getBytes(rawMqParam.getCharset());
        // m.messageId = MQC.MQMI_NONE;
        // 设置编码与mq服务一致
        m.encoding = getCcsid();
        // 设置字符集与mq服务一致
        m.characterSet = getCcsid();
        m.report = 128;
        return m;
    }

    /**
     * 定义队列管理器
     *
     * @return
     */
    public MQQueueManager getMQQueueManager() {
        // 初始队列管理器，使用动态参数配置
        try {
            MQQueueManager qm =
                    new MQQueueManager(getQueueManager(), buildCfg(), mqSimpleConnectionManager);
            return qm;
        } catch (MQException e) {
            log.error(e.getMessage(), e);
            throw new JoeyLeeException(ResultCode.MQ_ERROR);
        }
    }

    /**
     * 定义传输队列
     *
     * @return
     */
    public MQQueue getRequestMQQueue() {
        try {
            MQQueue requestMQQueue = getMQQueueManager().accessQueue(getRequestQueue(), MQC.MQOO_OUTPUT);
            return requestMQQueue;
        } catch (MQException e) {
            log.error(e.getMessage(), e);
            throw new JoeyLeeException(ResultCode.MQ_ERROR);
        }
    }

    /**
     * 定义接收队列
     *
     * @return
     */
    public MQQueue getReplyMQQueue() {
        try {
            String queueName = getReplyQueue();
            MQQueue replyMQQueue =
                    getMQQueueManager()
                            .accessQueue(
                                    queueName,
                                    MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_INQUIRE | MQC.MQOO_FAIL_IF_QUIESCING);
            log.debug("receiveQueue: {} curDepth: {}", queueName, replyMQQueue.getCurrentDepth());
            return replyMQQueue;
        } catch (MQException e) {
            log.error(e.getMessage(), e);
            throw new JoeyLeeException(ResultCode.MQ_ERROR);
        }
    }

    /**
     * 定义传输参数
     *
     * @return
     */
    public MQPutMessageOptions getPutMessageOptions() {
        // 初始化发送配置
        MQPutMessageOptions putMessageOptions = new MQPutMessageOptions();
        // pmo.options = pmo.options + MQC.MQPMO_NEW_MSG_ID;  //每次发送前自动生成唯一消息id
        return putMessageOptions;
    }

    /**
     * 定义接收参数
     *
     * @return
     */
    public MQGetMessageOptions getGetMessageOptions() {
        // 初始化接收配置
        //            MQC.MQOO_FAIL_IF_QUIESCING: 如果队列管理器停顿则取消息失败
        //            MQC.MQOO_INPUT_AS_Q_DEF:读取后消息移除队列
        //            MQC.MQOO_BROWSE:读取后消息仍保留在队列
        //            MQC.MQGMO_WAIT:队列没消息时等待，直到有消息才返回
        //            MQC.MQOO_INQUIRE:获取队列深度
        //            queue = qm.accessQueue(config.getRequestQueue(), MQC.MQOO_OUTPUT |
        // MQC.MQPMO_NEW_MSG_ID | MQC.MQOO_FAIL_IF_QUIESCING);
        MQGetMessageOptions getMessageOptions = new MQGetMessageOptions();
        // 设置等待的时间限制.
        // MQQueue.get() 调用等待合适消息到达的最长时间（以毫秒为单位）。它与 MQC.MQGMO_WAIT 结合使用。MQC.MQWI_UNLIMITED 值表示需要无限等待。
        // gmo.waitInterval = MQC.MQWI_UNLIMITED;
        getMessageOptions.waitInterval = getReceiveTimeOut();
        return getMessageOptions;
    }

    /**
     * 建立配置
     *
     * @return
     */
    public Hashtable<String, Object> buildCfg() {
        Hashtable<String, Object> cfg = new Hashtable<>();
        cfg.put("hostname", getHost());
        cfg.put("port", getPort());
        if (ObjectUtil.isNotEmpty(getUsername())) {
            cfg.put("userID", getUsername());
        }

        if (ObjectUtil.isNotEmpty(getPassword())) {
            cfg.put("password", getPassword());
        }

        if (ObjectUtil.isNotEmpty(getCcsid())) {
            cfg.put("CCSID", getCcsid());
        } else {
            // 默认用UTF8
            cfg.put("CCSID", 1208);
        }
        cfg.put("channel", getChannel());
        return cfg;
    }

    @Override
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory() {
        return null;
    }

    @Override
    public String toString() {
        String sb =
                "MqEnvConfig{"
                        + "mqSimpleConnectionManager="
                        + mqSimpleConnectionManager
                        + ", host='"
                        + host
                        + '\''
                        + ", port="
                        + port
                        + ", username='"
                        + username
                        + '\''
                        + ", password='"
                        + password
                        + '\''
                        + ", queueManager='"
                        + queueManager
                        + '\''
                        + ", ccsid="
                        + ccsid
                        + ", requestQueue='"
                        + requestQueue
                        + '\''
                        + ", replyQueue='"
                        + replyQueue
                        + '\''
                        + ", encoding='"
                        + encoding
                        + '\''
                        + ", channel='"
                        + channel
                        + '\''
                        + ", charSet='"
                        + charSet
                        + '\''
                        + ", receiveTimeOut="
                        + receiveTimeOut
                        + '}';
        return sb;
    }
}
