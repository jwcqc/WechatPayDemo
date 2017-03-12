
use yjy;

create table t_role (
  roleId varchar(32) primary key,
  roleName varchar(30) not null,
  roleDesc varchar(200)
);

create table t_level (
  levelId varchar(32) primary key,
  levelName varchar(30) not null,
  levelDesc varchar(200)
);

create table t_user (
  userId varchar(32) primary key,
  username varchar(32) not null,
  password varchar(32) not null,
  realName varchar(30),
  roleId varchar(32) references t_role(roleId),
  levelId varchar(32) references t_level(levelId),
  avator varchar(50),
  money int
);

create table t_document (
  docId varchar(32) primary key,
  originalName varchar(50) not null,
  saveName varchar(50) not null,
  savePath varchar(50) not null,
  downPath varchar(100) not null,
  type varchar(32),
  uploadTime datetime,
  publishStatus int(2) not null,  -- 0代表已上传unpublished，1代表已发布published
  needPay int(2) not null, -- 0代表免费课件，1代表课件需要付费下载
  money int,        -- 课件的具体费用
  userId varchar(32) references t_user(userId),
  levelId varchar(32) references t_level(levelId)
);


INSERT INTO t_level VALUES ('1', '小学', NULL);
INSERT INTO t_level VALUES ('2', '初中', NULL);
INSERT INTO t_level VALUES ('3', '高中', NULL);
INSERT INTO t_level VALUES ('4', '大学', NULL);
INSERT INTO t_level VALUES ('5', '成教', NULL);
INSERT INTO t_level VALUES ('6', '职教', NULL);

INSERT INTO t_role VALUES ('1', '老师', NULL);
INSERT INTO t_role VALUES ('2', '学生', NULL);


