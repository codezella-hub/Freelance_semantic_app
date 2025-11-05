export interface Payment {
    id?: number;
    contractId: number;
    amount: number;
    paymentDate: Date;
    status: PaymentStatus;
    paymentMethod: PaymentMethod;
    description?: string;
}

export enum PaymentStatus {
    PENDING = 'PENDING',
    COMPLETED = 'COMPLETED',
    FAILED = 'FAILED',
    REFUNDED = 'REFUNDED'
}

export enum PaymentMethod {
    CREDIT_CARD = 'CREDIT_CARD',
    BANK_TRANSFER = 'BANK_TRANSFER',
    PAYPAL = 'PAYPAL',
    CRYPTO = 'CRYPTO'
}