
package com.itheima.restkeeper.constant;


/**
 * @Description 静态变量
 */
public class SuperConstant {

	/**
	 * 主键名称
	 */
	public static final String id = "id";

	/**
	 * 常量是
	 */
	public static final String YES = "YES";

	/**
	 * @Description 前端显示布局
	 */
	public static final String COMPONENT_LAYOUT="Layout";

	/**
	 * 常量否
	 */
	public static final String NO = "NO";


	/**
	 * 树形根节点父Id
	 */
	public static final Long ROOT_PARENT_ID = -1L;

	/**
	 * 树形根节点父Id
	 */
	public static final String ROOT_PARENT_NAME = "资源管理";

	/**
	 * 常量菜单
	 */
	public static final String MENU = "MENU";

	/**
	 * jwt请求key
	 */
	public static final String JWT_TOKEN_HEADER = "jwtToken";


	/**
	 * store请求key
	 */
	public static final String STORE_TOKEN_HEADER = "storeToken";


	/***
	 * 当前用户
	 */
	public static final String CURRENT_USER ="current_user";

	/***
	 * 当前门店
	 */
	public static final String CURRENT_STORE ="current_store";


	/***
	 * 商户号ID字段
	 */
	public static final String ENTERPRISE_ID ="enterprise_id";

	/***
	 * 商户号ID字段
	 */
	public static final String STORE_ID ="store_id";

	/***
	 * 待付款【平台交易单及订单状态】
	 */
	public static final String DFK ="DFK";

	/***
	 * 付款中【平台交易单及订单状态】
	 */
	public static final String FKZ ="FKZ";

	/***
	 * 已结算【平台交易单及订单状态】
	 */
	public static final String YJS ="YJS";

	/***
	 * 取消订单【平台交易单及订单状态】
	 */
	public static final String QXDD ="QXDD";

	/***
	 * 取消订单【平台交易单及订单状态】
	 */
	public static final String MD ="MD";

	/***
	 * WAIT_BUYER_PAY【阿里订单状态：交易创建，等待买家付款】
	 */
	public static final String ALI_WAIT_BUYER_PAY ="WAIT_BUYER_PAY";

	/***
	 * TRADE_CLOSED【阿里订单状态：未付款交易超时关闭，或支付完成后全额退款】
	 */
	public static final String ALI_TRADE_CLOSED ="TRADE_CLOSED";

	/***
	 * TRADE_SUCCESS【阿里订单状态：交易支付成功】
	 */
	public static final String ALI_TRADE_SUCCESS="TRADE_SUCCESS";

	/***
	 * TRADE_FINISHED【阿里订单状态：交易结束，不可退款）
	 */
	public static final String ALI_TRADE_FINISHED ="TRADE_FINISHED";

	/***
	 * 桌台空闲
	 */
	public static final String FREE = "FREE";

	/***
	 * 桌台使用
	 */
	public static final String USE = "USE";

	/***
	 * 订单项目移除
	 */
	public static final  String OPERTION_TYPE_REMOVE = "REMOVE";

	/***
	 * 订单项目添加
	 */
	public static final  String OPERTION_TYPE_ADD = "ADD";

	/***
	 * 交易渠道:阿里支付
	 */
	public static final String TRADING_CHANNEL_ALIPAY = "ALIPAY";

	/***
	 * 交易渠道:现金支付
	 */
	public static final String TRADING_CHANNEL_CASHPAY = "CASHPAY";

	/**
	 * 交易渠道:免单
	 */
	public static final String TRADING_CHANNEL_FREE_CHARGE = "FREE_CHARGE";

	/***
	 * 交易渠道:退款
	 */
	public static final String TRADING_CHANNEL_REFUND= "REFUND";

	/***
	 * 交易类型:付款
	 */
    public static final String TRADING_TYPE_FK = "FK";

	/***
	 * 交易类型:退款
	 */
	public static final String TRADING_TYPE_TK = "TK";

	/***
	 * 交易类型:付款
	 */
	public static final String TRADING_TYPE_MD = "MD";
	/***
	 * 企业状态:试用
	 */
	public static final String TRIAL = "TRIAL";
	/***
	 * 企业状态:正式
	 */
	public static final String OFFICIAL = "OFFICIAL";
	/***
	 * 企业状态:停用
	 */
    public static final String STOP = "STOP";
	/***
	 * 企业状态:拒绝
	 */
	public static final String REFUSE = "REFUSE";
}
