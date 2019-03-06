--drop table accounts if exists;
create table if not exists accounts(account  varchar(25) NOT NULL,
				password varchar(256),
				name     varchar(25),
				nickName varchar(25),
				email    varchar(25),
				tel      varchar(15),
				gender   int,
				info     varchar(256),
				website  varchar(48),
				grade    int,
				privilege int,
				regdate  date,
				loginDate date,
				valid     bool,
				moderate  int,
				ireserve  int,
				sreserve  varchar(48),
				primary key (account));

--drop table docs if exists;
create table if not exists docs(id int NOT NULL,
				title      varchar(50),
				author     varchar(25),
				email      varchar(25),
				category   varchar(25),
				brief      varchar(256),
				cid        int, /*category id*/
				repost     bool,
				onTop      bool,
				score      int,
				positive   int,
				negative   int,
				text       varchar(1048576),
				cdate     timestamp,
				mdate     timestamp,
				resid      int,
				account    varchar(25),
				next       int,
				previous   int,
				views  int,
				refCount   int,
				ireserve   int,
				sreserve  varchar(48),
				url        varchar(128),
				microBlog  varchar(128),
				microMessage varchar(128),
				primary key (id));

--drop table draft if exists;
create table if not exists draft(id int NOT NULL,
				did 	   int,
				account    varchar(25),
				author     varchar(25),
				cid        int,
				title      varchar(50),
				category   varchar(25),
				repost     bool,
				onTop      bool,
				text       varchar(1048576),
				resid      int,
				url       varchar(128),
				ireserve  int,
				sreserve  varchar(48),
				primary key (id));

--drop table docres if exists;
create table if not exists docres(id int NOT NULL,
				did 	   int,
				draftId    int,
				ftype      varchar(15),
				fname      varchar(20),
				url	   	   varchar(50),
				data       blob,
				ireserve  int,
				sreserve  varchar(48),
				primary key (id));

--drop table mydoc if exists;
create table if not exists mydoc(did 	int NOT NULL,
				account varchar(25) NOT NULL,
				score   	int,
				fav			bool,
				ireserve  int,
				sreserve  varchar(48),
				primary key (did));
								
--drop table menutree if exists;
create table if not exists menutree(id int NOT NULL,
				level 		int,
				parent 		int,
				name		varchar(25),
				primary key	(id));

--drop table doccomment if exists;
create table if not exists doccomment(id int NOT NULL,
				did 	    int,
				parent 		int,
				account     varchar(25),
				author      varchar(25),
				text        varchar(512),
				cdate       timestamp,
				score   	int,
				ireserve  int,
				sreserve  varchar(48),
				primary key	(id));

--drop table docvisitors if exists;
create table if not exists docvisitors(ip varchar(50) NOT NULL,
				did 	    int,
				vdate  		date,
				ireserve  int,
				sreserve  varchar(48),
				primary key	(ip));

--drop table questions if exists;				
create table if not exists questions(qid int NOT NULL,
				title      varchar(100),
				account    varchar(25),
				author     varchar(25),
				text       varchar(512),
				category   varchar(25),
				cdate      timestamp,
				invitees   varchar(128),
				ireserve  int,
				sreserve  varchar(48),
				primary key	(qid));

--drop table tags if exists;
create table if not exists tags(tag varchar(50),
				primary key (tag));

--drop table QuestionTags if exists;
create table if not exists QuestionTags(qid int NOT NULL,
				tag varchar(50),
				primary key (qid, tag));

--drop table mytag if exists;
create table if not exists mytag(account varchar(25) NOT NULL,
				tag varchar(50),
				primary key (tag, account));

--drop table QuestionAnswers if exists;
create table if not exists QuestionAnswers(id int NOT NULL,
				qid        int,
				parent 	   int,
				account    varchar(25),
				author     varchar(25),
				text       varchar(10240),
				cdate      timestamp,
				score      int,
				ireserve  int,
				sreserve  varchar(48),
				primary key	(id));

--drop table UserMessages if exists;
create table if not exists UserMessages(id int NOT NULL,
				account     varchar(25),
				author      varchar(25),
				text        varchar(512),
				cdate       timestamp,
				ireserve  int,
				sreserve  varchar(48),
				primary key	(id));

--drop table DocTree if exists;
create table if not exists DocTree(id int NOT NULL,
				pid         int,            /* parent id */
				text        varchar(64),
				state       varchar(8),     /* open | close */
				checked     bool, 
				iconCls     varchar(32),    /* icon-save | icon-add | .. */
				attributes  varchar(512),   /* "url":"/xx","price":100 */
				dirFlag     bool,           /* true: directory */
				
				/* extra information  */
				cid         int,             /* doc category id */
				did         int,             /* doc id */
				primary key	(id));

				