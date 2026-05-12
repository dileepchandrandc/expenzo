import type { YearMonth } from "../models";

export const getFormattedDate = (strDate: string): string  => {
    const date = new Date(strDate + 'Z');
    try {
        return new Intl.DateTimeFormat('en-IN', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
        }).format(date);
    } catch(err) {
        console.log("Error while parsing the date " + err)
        return "";
    }
}

export const getCurrentYearAndMonth = (): YearMonth => {
    const now = new Date();
    return {
        year: now.getFullYear(),
        month: now.getMonth() + 1
    }
}

export const getPaymentChannelLabel = (channel: string) => {
    if (channel == 'CREDIT_CARD') return 'Credit Card';
    else if (channel == 'DEBIT_CARD') return 'Debit card';
    else if (channel == 'BANK_ACCOUNT') return 'Bank Account';
    else if (channel == 'WALLTER') return 'Wallet';
    return '';
}
