/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 5.7.3-m13 : Database - ontologydb2
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

USE `ontologydb`;

/*Table structure for table `update_data_lines` */

CREATE TABLE `update_data_lines` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `infos` varchar(200) DEFAULT NULL,
  `label` varchar(200) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_no_value` */

CREATE TABLE `update_dict_no_value` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_change` */

CREATE TABLE `update_dict_onto_change` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_class` */

CREATE TABLE `update_dict_onto_class` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_date` */

CREATE TABLE `update_dict_onto_date` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_keyword` */

CREATE TABLE `update_dict_onto_keyword` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_logic` */

CREATE TABLE `update_dict_onto_logic` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `logic_type` varchar(20) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_qword` */

CREATE TABLE `update_dict_onto_qword` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_sort` */

CREATE TABLE `update_dict_onto_sort` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `value_type` varchar(20) DEFAULT NULL,
  `descending` varchar(10) DEFAULT NULL,
  `k` int(11) DEFAULT NULL,
  `is_top_k` varchar(10) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_special` */

CREATE TABLE `update_dict_onto_special` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `msg` varchar(200) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_techop` */

CREATE TABLE `update_dict_onto_techop` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_techperiod` */

CREATE TABLE `update_dict_onto_techperiod` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_trigger` */

CREATE TABLE `update_dict_onto_trigger` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  `direction` varchar(10) DEFAULT NULL,
  `isindex` varchar(10) DEFAULT NULL,
  `skiplist` varchar(50) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_onto_vaguenum` */

CREATE TABLE `update_dict_onto_vaguenum` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `vague_type` varchar(20) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `update_dict_trans` */

CREATE TABLE `update_dict_trans` (
  `text` varchar(200) DEFAULT NULL,
  `cate` varchar(50) DEFAULT NULL,
  `seg` varchar(200) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  `operation` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/* Trigger structure for table `data_lines` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `data_lines_after_delete_trigger` AFTER DELETE ON `data_lines` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'data_lines') ;
    
  delete from update_data_lines where text = old.text;
  insert ignore into update_data_lines (text,operation) values(old.text,'D');
  
  END */$$


DELIMITER ;

/* Trigger structure for table `data_lines` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `data_lines_after_insert_trigger` AFTER INSERT ON `data_lines` FOR EACH ROW BEGIN
  insert ignore into dict_update_recode (text, tableName) 
  values
    (new.text, 'data_lines') ;
  INSERT INTO update_data_lines (TEXT,cate,seg,infos,label,operation) 
  SELECT l.text,l.cate,l.seg,l.infos,t.label,'I' FROM data_lines l,data_type t 
  WHERE l.type_id = t.id AND l.id = new.id;  
END */$$


DELIMITER ;

/* Trigger structure for table `data_lines` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `data_lines_after_update_trigger` AFTER UPDATE ON `data_lines` FOR EACH ROW BEGIN
  insert ignore into dict_update_recode (text, tableName) 
  values
    (new.text, 'data_lines') ;
    
  insert ignore into dict_update_recode (text, tableName) 
  values
    (old.text, 'data_lines') ;  
  
  delete from update_data_lines where text = old.text;
  INSERT INTO update_data_lines (TEXT,cate,seg,infos,label,operation) 
  SELECT l.text,l.cate,l.seg,l.infos,t.label,'U' FROM data_lines l,data_type t 
  WHERE l.type_id = t.id AND l.id = new.id;
  if (new.text != old.text) then
	insert ignore into update_data_lines(text,operation) values (old.text,'D');
  end if;  
  
END */$$


DELIMITER ;

/* Trigger structure for table `dict_no_value` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_no_value_after_delete_trigger` AFTER DELETE ON `dict_no_value` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_no_value') ;
    
   DELETE FROM update_dict_no_value WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_no_value (TEXT,operation)
    VALUES (old.text,'D'); 
   
  end */$$


DELIMITER ;

/* Trigger structure for table `dict_no_value` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_no_value_after_insert_trigger` AFTER INSERT ON `dict_no_value` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_no_value') ;
    
INSERT IGNORE INTO update_dict_no_value(TEXT,cate,seg,operation)
    VALUES (new.text,new.cate,new.seg,'I');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_no_value` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_no_value_after_update_trigger` AFTER UPDATE ON `dict_no_value` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_no_value') ;  
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_no_value') ;  
    
DELETE FROM update_dict_no_value WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_no_value(TEXT,cate,seg,operation)
    VALUES (new.text,new.cate,new.seg,'U');
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_no_value(TEXT,operation) VALUES(old.text,'D');
END IF;    
        
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_change` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_change_after_delete_trigger` AFTER DELETE ON `dict_onto_change` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_change') ;
DELETE FROM update_dict_onto_change WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_change (TEXT,operation)
    VALUES (old.text,'D');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_change` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_change_after_insert_trigger` AFTER INSERT ON `dict_onto_change` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_change') ;
    
INSERT IGNORE INTO update_dict_onto_change(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'I');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_change` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_change_after_update_trigger` AFTER UPDATE ON `dict_onto_change` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_change') ;  
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_change') ;    
    
DELETE FROM update_dict_onto_change WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_change(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'U');
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_change(TEXT,operation) VALUES(old.text,'D');
END IF;    
     
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_class` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_class_after_delete_trigger` AFTER DELETE ON `dict_onto_class` FOR EACH ROW BEGIN
  insert ignore into dict_update_recode (text, tableName) 
  values
    (old.text, 'dict_onto_class') ;
    
   DELETE FROM update_dict_onto_class WHERE TEXT=old.text;
   INSERT IGNORE INTO update_dict_onto_class (TEXT,operation)
    VALUES (old.text,'D');
END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_class` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_class_after_insert_trigger` AFTER INSERT ON `dict_onto_class` FOR EACH ROW BEGIN
  insert ignore into dict_update_recode (text, tableName) 
  values
    (new.text, 'dict_onto_class') ;
    
   INSERT IGNORE INTO update_dict_onto_class(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'I');
END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_class` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_class_after_update_trigger` AFTER UPDATE ON `dict_onto_class` FOR EACH ROW BEGIN
  insert ignore into dict_update_recode (text, tableName) 
  values
    (new.text, 'dict_onto_class') ;
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_class') ;    
    
   DELETE FROM update_dict_onto_class WHERE TEXT=old.text;
   INSERT IGNORE INTO update_dict_onto_class(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'U');
   IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_class(TEXT,operation) VALUES(old.text,'D');
   END IF;
END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_date` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_date_after_delete_trigger` AFTER DELETE ON `dict_onto_date` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_date') ;
    
DELETE FROM update_dict_onto_date WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_date (TEXT,operation)
    VALUES (old.text,'D');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_date` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_date_after_insert_trigger` AFTER INSERT ON `dict_onto_date` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_date') ;
    
INSERT IGNORE INTO update_dict_onto_date(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'I');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_date` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_date_after_update_trigger` AFTER UPDATE ON `dict_onto_date` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_date') ;  
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_date') ;      
    
DELETE FROM update_dict_onto_date WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_date(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'U');
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_date(TEXT,operation) VALUES(old.text,'D');
END IF;    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_keyword` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_keyword_after_delete_trigger` AFTER DELETE ON `dict_onto_keyword` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_keyword') ;
    
    DELETE FROM update_dict_onto_keyword WHERE TEXT=old.text;
   INSERT IGNORE INTO update_dict_onto_keyword (TEXT,operation)
    VALUES (old.text,'D');
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_keyword` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_keyword_after_insert_trigger` AFTER INSERT ON `dict_onto_keyword` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_keyword') ;
    
    INSERT IGNORE INTO update_dict_onto_keyword(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'I');
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_keyword` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_keyword_after_update_trigger` AFTER UPDATE ON `dict_onto_keyword` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_keyword') ;  
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_keyword') ;     
    
DELETE FROM update_dict_onto_keyword WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_keyword(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'U');
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_keyword(TEXT,operation) VALUES(old.text,'D');
END IF;    
     
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_logic` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_logic_after_delete_trigger` AFTER DELETE ON `dict_onto_logic` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_logic') ;
    
    DELETE FROM update_dict_onto_logic WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_logic (TEXT,operation)
    VALUES (old.text,'D');
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_logic` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_logic_after_insert_trigger` AFTER INSERT ON `dict_onto_logic` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_logic') ;
    
INSERT IGNORE INTO update_dict_onto_logic(TEXT,cate,seg,logic_type,operation)
    VALUES (new.text,new.cate,new.seg,new.logic_type,'I');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_logic` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_logic_after_update_trigger` AFTER UPDATE ON `dict_onto_logic` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_logic') ;  
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_logic') ;      
    
DELETE FROM update_dict_onto_logic WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_logic(TEXT,cate,seg,logic_type,operation)
    VALUES (new.text,new.cate,new.seg,new.logic_type,'U');
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_logic(TEXT,operation) VALUES(old.text,'D');
END IF;    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_qword` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_qword_after_delete_trigger` AFTER DELETE ON `dict_onto_qword` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_qword') ;
    
DELETE FROM update_dict_onto_qword WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_qword (TEXT,operation)
    VALUES (old.text,'D');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_qword` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_qword_after_insert_trigger` AFTER INSERT ON `dict_onto_qword` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_qword') ;
    
INSERT IGNORE INTO update_dict_onto_qword(TEXT,cate,seg,type,operation)
    VALUES (new.text,new.cate,new.seg,new.type,'I');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_qword` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_qword_after_update_trigger` AFTER UPDATE ON `dict_onto_qword` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_qword') ;  
    
DELETE FROM update_dict_onto_qword WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_qword(TEXT,cate,seg,type,operation)
    VALUES (new.text,new.cate,new.seg,new.type,'U');
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_qword(TEXT,operation) VALUES(old.text,'D');
END IF;    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_sort` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_sort_after_delete_trigger` AFTER DELETE ON `dict_onto_sort` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_sort') ;
    
DELETE FROM update_dict_onto_sort WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_sort (TEXT,operation)
    VALUES (old.text,'D');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_sort` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_sort_after_insert_trigger` AFTER INSERT ON `dict_onto_sort` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_sort') ;
    
INSERT IGNORE INTO update_dict_onto_sort(TEXT,cate,seg,value_type,descending,k,is_top_k,operation)
    VALUES (new.text,new.cate,new.seg,new.value_type,new.descending,new.k,new.is_top_k,'I');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_sort` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_sort_after_update_trigger` AFTER UPDATE ON `dict_onto_sort` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_sort') ;  
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_sort') ;      
    
DELETE FROM update_dict_onto_sort WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_sort(TEXT,cate,seg,value_type,descending,k,is_top_k,operation)
    VALUES (new.text,new.cate,new.seg,new.value_type,new.descending,new.k,new.is_top_k,'I'); 
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_sort(TEXT,operation) VALUES(old.text,'D');
END IF;    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_special` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_special_after_delete_trigger` AFTER DELETE ON `dict_onto_special` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_special') ;
    
DELETE FROM update_dict_onto_special WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_special (TEXT,operation)
    VALUES (old.text,'D');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_special` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_special_after_insert_trigger` AFTER INSERT ON `dict_onto_special` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_special') ;
    
INSERT IGNORE INTO update_dict_onto_special(TEXT,cate,seg,msg,operation)
    VALUES (new.text,new.cate,new.seg,new.msg,'I');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_special` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_special_after_update_trigger` AFTER UPDATE ON `dict_onto_special` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_special') ;  
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_special') ;   
    
DELETE FROM update_dict_onto_special WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_special(TEXT,cate,seg,msg,operation)
    VALUES (new.text,new.cate,new.seg,new.msg,'U');
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_special(TEXT,operation) VALUES(old.text,'D');
END IF;    
       
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_techop` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_techOp_after_delete_trigger` AFTER DELETE ON `dict_onto_techop` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_techOp') ;
    
DELETE FROM update_dict_onto_techop WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_techop (TEXT,operation)
    VALUES (old.text,'D');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_techop` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_techOp_after_insert_trigger` AFTER INSERT ON `dict_onto_techop` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_techOp') ;
    
INSERT IGNORE INTO update_dict_onto_techop(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'I');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_techop` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_techOp_after_update_trigger` AFTER UPDATE ON `dict_onto_techop` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_techOp') ;  
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_techOp') ;     
    
DELETE FROM update_dict_onto_techop WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_techop(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'U');
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_techop(TEXT,operation) VALUES(old.text,'D');
END IF;    
     
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_techperiod` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_techPeriod_after_delete_trigger` AFTER DELETE ON `dict_onto_techperiod` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_techPeriod') ;
    
DELETE FROM update_dict_onto_techperiod WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_techperiod (TEXT,operation)
    VALUES (old.text,'D');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_techperiod` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_techPeriod_after_insert_trigger` AFTER INSERT ON `dict_onto_techperiod` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_techPeriod') ;
    
INSERT IGNORE INTO update_dict_onto_techperiod(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'I');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_techperiod` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_techPeriod_after_update_trigger` AFTER UPDATE ON `dict_onto_techperiod` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_techPeriod') ;  
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_techPeriod') ;      
    
DELETE FROM update_dict_onto_techperiod WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_techperiod(TEXT,cate,seg,VALUE,operation)
    VALUES (new.text,new.cate,new.seg,new.value,'U');
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_techperiod(TEXT,operation) VALUES(old.text,'D');
END IF;    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_trigger` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_trigger_after_delete_trigger` AFTER DELETE ON `dict_onto_trigger` FOR EACH ROW BEGIN
  insert ignore into dict_update_recode (text, tableName) 
  values
    (old.text, 'dict_onto_trigger') ;
    
DELETE FROM update_dict_onto_trigger WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_trigger (TEXT,operation)
    VALUES (old.text,'D');    
    
END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_trigger` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_trigger_after_insert_trigger` AFTER INSERT ON `dict_onto_trigger` FOR EACH ROW BEGIN
  insert ignore into dict_update_recode (text, tableName) 
  values
    (new.text, 'dict_onto_trigger') ;
    
INSERT IGNORE INTO update_dict_onto_trigger(TEXT,cate,seg,VALUE,direction,isindex,skiplist,operation)
    VALUES (new.text,new.cate,new.seg,new.value,new.direction,new.isindex,new.skiplist,'I');    
    
END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_trigger` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_trigger_after_update_trigger` AFTER UPDATE ON `dict_onto_trigger` FOR EACH ROW BEGIN
  insert ignore into dict_update_recode (text, tableName) 
  values
    (new.text, 'dict_onto_trigger') ;
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_trigger') ;    
    
DELETE FROM update_dict_onto_trigger WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_trigger(TEXT,cate,seg,VALUE,direction,isindex,skiplist,operation)
    VALUES (new.text,new.cate,new.seg,new.value,new.direction,new.isindex,new.skiplist,'I');    
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_trigger(TEXT,operation) VALUES(old.text,'D');
END IF;    
    
END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_vaguenum` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_vagueNum_after_delete_trigger` AFTER DELETE ON `dict_onto_vaguenum` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_vagueNum') ;
    
DELETE FROM update_dict_onto_vaguenum WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_vaguenum (TEXT,operation)
    VALUES (old.text,'D');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_vaguenum` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_vagueNum_after_insert_trigger` AFTER INSERT ON `dict_onto_vaguenum` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_vagueNum') ;
    
INSERT IGNORE INTO update_dict_onto_vaguenum(TEXT,cate,seg,vague_type,operation)
    VALUES (new.text,new.cate,new.seg,new.vague_type,'I');    
    
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_onto_vaguenum` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_onto_vagueNum_after_update_trigger` AFTER UPDATE ON `dict_onto_vaguenum` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_onto_vagueNum') ;  
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_onto_vagueNum') ;    
    
DELETE FROM update_dict_onto_vaguenum WHERE TEXT=old.text;
INSERT IGNORE INTO update_dict_onto_vaguenum(TEXT,cate,seg,vague_type,operation)
    VALUES (new.text,new.cate,new.seg,new.vague_type,'U');
IF (old.text != new.text) THEN
    INSERT IGNORE INTO update_dict_onto_vaguenum(TEXT,operation) VALUES(old.text,'D');
END IF;    
      
  END */$$


DELIMITER ;

/* Trigger structure for table `dict_trans` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_trans_after_delete_trigger` AFTER DELETE ON `dict_trans` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_trans') ;
    
    delete from update_dict_trans where text=old.text;
    INSERT IGNORE INTO update_dict_trans (TEXT,operation)
    VALUES (old.text,'D');
    
END */$$


DELIMITER ;

/* Trigger structure for table `dict_trans` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_trans_after_insert_trigger` AFTER INSERT ON `dict_trans` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_trans') ;
    
   insert ignore into update_dict_trans(text,cate,seg,value,operation)
   values (new.text,new.cate,new.seg,new.value,'I');
END */$$


DELIMITER ;

/* Trigger structure for table `dict_trans` */

DELIMITER $$

/*!50003 CREATE */ /*!50017 DEFINER = 'qnateam'@'%' */ /*!50003 TRIGGER `dict_trans_after_update_trigger` AFTER UPDATE ON `dict_trans` FOR EACH ROW BEGIN
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (new.text, 'dict_trans') ;
    
  INSERT IGNORE INTO dict_update_recode (TEXT, tableName) 
  VALUES
    (old.text, 'dict_trans') ;    
    
  DELETE FROM update_dict_trans WHERE TEXT=old.text;
  INSERT IGNORE INTO update_dict_trans(TEXT,cate,seg,VALUE,operation)
   VALUES (new.text,new.cate,new.seg,new.value,'U');
   IF (old.text != new.text) THEN
	INSERT IGNORE INTO update_dict_trans(TEXT,operation) VALUES(old.text,'D');
  END IF;
   
END */$$


DELIMITER ;
