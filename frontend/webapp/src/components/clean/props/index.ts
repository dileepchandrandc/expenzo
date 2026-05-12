
export interface CleanHorizndalProps {
    value: number;
    maxValue: number;
    color?: string;
    bgColor?: string;
    height?: number;
}

export interface CleanModalProps {
    title?: string;
    onClose: VoidFunction;
    minWidth?: number;
}