-- 保险客户信息表
DROP ALTER IF EXISTS insurance_customer_info;
CREATE TABLE insurance_customer_info(
  cust_id int NOT NULL comment '客户id',
  name varchar(15) NOT NULL comment'客户姓名',
  staff_id int DEFAULT NULL comment'关联业务员id'
  policy_number int DEFAULT NULL comment'保单数量'
  PRIMARY KEY (cust_id),
  KEY staff_id (staff_id)
);

-- 保单表
DROP ALTER IF EXISTS insurance_policy;
CREATE TABLE insurance_policy(
  p_id int NOT NULL comment '保单id',
  project_name varchar(15) NOT NULL comment'保单项目名称',
  p_no int(11) NOT NULL comment'保单序号',
  p_type varchar(11) NULL comment'保单类型',
  insured_amount decimal(8,2) NOT NULL comment'保险金额',
  available_amount decimal(8,2) NOT NULL comment'可用保险赔偿金额',
  cust_id int NOT NULL comment'关联客户id'
  PRIMARY KEY (p_id),
  KEY cust_id (cust_id)
);

-- 理赔记录表
DROP ALTER IF EXISTS insurance_claims;
CREATE TABLE insurance_claims(
  c_id int NOT NULL comment '理赔记录id',
  cust_id int NOT NULL comment'关联保单id',
  claims_approve_result NOT NULL comment '理赔结果',
  claims_approve_amount NOT NULL comment '理赔批准金额',
  claims_reject_reason DEFAULT NULL  comment '理赔拒绝原因'
  PRIMARY KEY (c_id)
);