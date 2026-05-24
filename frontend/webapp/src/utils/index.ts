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

export const hexToRgba = (hex: string, opacity = 1) => {
  hex = hex.replace('#', '');

  const r = parseInt(hex.substring(0, 2), 16);
  const g = parseInt(hex.substring(2, 4), 16);
  const b = parseInt(hex.substring(4, 6), 16);

  return `rgba(${r}, ${g}, ${b}, ${opacity})`;
}
