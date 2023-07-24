alter table RequestInfo add priority int null
go
ALTER TABLE ProcessInstanceLog ADD processType int null
go

update ProcessInstanceLog set processType = 1
go
update RequestInfo set priority = 5
go

create table CaseIdInfo (
    id bigint identity not null,
    caseIdPrefix varchar(255) null,
    currentValue bigint null,
    primary key (id)
) lock datarows
go

create table CaseRoleAssignmentLog (
    id bigint identity not null,
    caseId varchar(255) null,
    entityId varchar(255) null,
    processInstanceId bigint not null,
    roleName varchar(255) null,
    type int not null,
    primary key (id)
) lock datarows
go

alter table CaseIdInfo 
    add constraint UK_CaseIdInfo_1 unique (caseIdPrefix)
go

ALTER TABLE NodeInstanceLog ADD referenceId bigint null
go
ALTER TABLE NodeInstanceLog ADD nodeContainerId varchar(255) null
go    

ALTER TABLE RequestInfo ADD processInstanceId bigint null
go

ALTER TABLE AuditTaskImpl ADD lastModificationDate datetime null
go
update AuditTaskImpl  set lastModificationDate = (
    select max(logTime) from TaskEvent where taskId=ati.taskId group by taskId
) from AuditTaskImpl ati
go

create table CaseFileDataLog (
    id bigint identity not null,
    caseDefId varchar(255) null,
    caseId varchar(255) null,
    itemName varchar(255) null,
    itemType varchar(255) null,
    itemValue varchar(255) null,
    lastModified datetime null,
    lastModifiedBy varchar(255) null,
    primary key (id)
) lock datarows
go

create table ExecutionErrorInfo (
    id bigint identity not null,
    ERROR_ACK smallint null,
    ERROR_ACK_AT datetime null,
    ERROR_ACK_BY varchar(255) null,
    ACTIVITY_ID bigint null,
    ACTIVITY_NAME varchar(255) null,
    DEPLOYMENT_ID varchar(255) null,
    ERROR_INFO text null,
    ERROR_DATE datetime null,
    ERROR_ID varchar(255) null,
    ERROR_MSG varchar(255) null,
    INIT_ACTIVITY_ID bigint null,
    JOB_ID bigint null,
    PROCESS_ID varchar(255) null,
    PROCESS_INST_ID bigint null,
    ERROR_TYPE varchar(255) null,
    primary key (id)
) lock datarows
go

create index IDX_ErrorInfo_pInstId on ExecutionErrorInfo(PROCESS_INST_ID)
go
create index IDX_ErrorInfo_errorAck on ExecutionErrorInfo(ERROR_ACK)
go

ALTER TABLE ProcessInstanceLog ADD sla_due_date datetime null
go
ALTER TABLE ProcessInstanceLog ADD slaCompliance int null
go    
ALTER TABLE NodeInstanceLog ADD sla_due_date datetime null
go
ALTER TABLE NodeInstanceLog ADD slaCompliance int null
go 