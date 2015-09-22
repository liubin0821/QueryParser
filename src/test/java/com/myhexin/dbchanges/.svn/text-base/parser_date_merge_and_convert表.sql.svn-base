/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 5.0.75-log : Database - configFile
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
USE `configFile`;

/*Table structure for table `parser_date_convert` */

CREATE TABLE `parser_date_convert` (
  `id` int(11) NOT NULL auto_increment,
  `text` varchar(1000) default NULL,
  `convert_text` varchar(1000) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Data for the table `parser_date_convert` */

insert  into `parser_date_convert`(`id`,`text`,`convert_text`) values (1,'20152月','2015年2月');
insert  into `parser_date_convert`(`id`,`text`,`convert_text`) values (2,'2015年每月','2015年');
insert  into `parser_date_convert`(`id`,`text`,`convert_text`) values (3,'2013年,1季度','2013年1季度');
insert  into `parser_date_convert`(`id`,`text`,`convert_text`) values (4,'即将在2014年','2014年即将');
insert  into `parser_date_convert`(`id`,`text`,`convert_text`) values (5,'即将2014年','2014年即将');

/*Table structure for table `parser_datemerge_rule` */

CREATE TABLE `parser_datemerge_rule` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `text` varchar(1000) NOT NULL,
  `need_replace` char(1) NOT NULL default 'N',
  `whole_text` varchar(1000) default NULL,
  `replace_rule` varchar(1000) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

/*Data for the table `parser_datemerge_rule` */

insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (1,'^半|每$#^(天|日|个?交易日|周|个?星期|个?礼拜|个?月|个?季度|年|个?年度)$','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (2,'连续#^(\\d{1,2}|[一二三四五六七八九十]{1,2})(?:个)?(年|季度|季|月|周|日|天)$','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (3,'^(\\d{1,2}|[一二三四五六七八九十]{1,2})(?:个)?(年|季度|季|月|周|日|天)$#连续','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (4,'(\\d\\d)?\\d\\d年?#第?[1234一二三四]季度?','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (5,'(198\\d|199\\d|200\\d|201\\d)(\\d{1,2})#月','Y','(198\\d|199\\d|200\\d|201\\d)(\\d{1,2})月','(1)年(2)月');
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (6,'(198\\d|199\\d|200\\d|201\\d|\\d{2})(年|年度|)#(\\d{1,2})月#(\\d{1,2})(日|号|)','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (8,'(198\\d|199\\d|200\\d|201\\d|\\d{2})(年|年度|)#[，|。|、|？|！|,|.| ]#^(\\d|[一二三四])(季度|季)$','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (9,'(198\\d|199\\d|200\\d|201\\d|\\d{2})(年|年度|)#每月','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (10,'^(上|下|本|这|这个)$#^(上|下|)(个?交易日|周|个?星期|个?礼拜|年|个?月|日|个?季度|个?年度|个?((周|礼拜|星期)([1-7一二三四五六日天]))|周末)$','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (11,'^(上|下|本|这|这个)$#个#^(上|下|)(个?交易日|周|个?星期|个?礼拜|年|个?月|日|个?季度|个?年度|个?((周|礼拜|星期)([1-7一二三四五六日天]))|周末)$','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (12,'^(上|下|)$#^(上|下|)$#^(上|下|)(个?交易日|周|个?星期|个?礼拜|年|个?月|日|个?季度|个?年度|个?((周|礼拜|星期)([1-7一二三四五六日天]))|周末)$','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (13,'^(.*?)(即将|将要|将)$#在#(198\\d|199\\d|200\\d|201\\d)(年|年度|)','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (14,'^(.*?)(即将|将要|将)$#(198\\d|199\\d|200\\d|201\\d)(年|年度|)','N',NULL,NULL);
insert  into `parser_datemerge_rule`(`id`,`text`,`need_replace`,`whole_text`,`replace_rule`) values (15,'(前|以前|之前|后|以后|以来|之后)#(\\d+?(?:天|周|月|季度))','N',NULL,NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
