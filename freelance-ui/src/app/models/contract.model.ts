export interface Contract {
    id?: number;
    title: string;
    description: string;
    startDate: Date;
    endDate: Date;
    amount: number;
    status: ContractStatus;
}

export enum ContractStatus {
    DRAFT = 'DRAFT',
    ACTIVE = 'ACTIVE',
    COMPLETED = 'COMPLETED',
    CANCELLED = 'CANCELLED'
}