alter table RequestInfo add priority int;
ALTER TABLE ProcessInstanceLog ADD processType int;

update ProcessInstanceLog set processType = 1;
update RequestInfo set priority = 5;

create table CaseIdInfo (
    id bigint identity not null,
    caseIdPrefix varchar(255),
    currentValue bigint,
    primary key (id)
);

create table CaseRoleAssignmentLog (
    id bigint identity not null,
    caseId varchar(255),
    entityId varchar(255),
    processInstanceId bigint not null,
    roleName varchar(255),
    type int not null,
    primary key (id)
);

alter table CaseIdInfo 
    add constraint UK_CaseIdInfo_1 unique (caseIdPrefix);
    
ALTER TABLE NodeInstanceLog ADD referenceId bigint;
ALTER TABLE NodeInstanceLog ADD nodeContainerId varchar(255);     

ALTER TABLE RequestInfo ADD processInstanceId bigint;

ALTER TABLE AuditTaskImpl ADD lastModificationDate datetime2;
update AuditTaskImpl set lastModificationDate = (
    select max(logTime) from TaskEvent where taskId=AuditTaskImpl.taskId group by taskId
);

create table CaseFileDataLog (
    id bigint identity not null,
    caseDefId varchar(255),
    caseId varchar(255),
    itemName varchar(255),
    itemType varchar(255),
    itemValue varchar(255),
    lastModified datetime2,
    lastModifiedBy varchar(255),
    primary key (id)
);

create table ExecutionErrorInfo (
    id bigint identity not null,
    ERROR_ACK smallint,
    ERROR_ACK_AT datetime2,
    ERROR_ACK_BY varchar(255),
    ACTIVITY_ID bigint,
    ACTIVITY_NAME varchar(255),
    DEPLOYMENT_ID varchar(255),
    ERROR_INFO varchar(MAX),
    ERROR_DATE datetime2,
    ERROR_ID varchar(255),
    ERROR_MSG varchar(255),
    INIT_ACTIVITY_ID bigint,
    JOB_ID bigint,
    PROCESS_ID varchar(255),
    PROCESS_INST_ID bigint,
    ERROR_TYPE varchar(255),
    primary key (id)
);

create index IDX_ErrorInfo_pInstId on ExecutionErrorInfo(PROCESS_INST_ID);
create index IDX_ErrorInfo_errorAck on ExecutionErrorInfo(ERROR_ACK);

ALTER TABLE ProcessInstanceLog ADD sla_due_date datetime2;
ALTER TABLE ProcessInstanceLog ADD slaCompliance int; 
ALTER TABLE NodeInstanceLog ADD sla_due_date datetime2;
ALTER TABLE NodeInstanceLog ADD slaCompliance int;   