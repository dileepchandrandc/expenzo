
import { User, LayoutDashboard } from '@lucide/vue';

export const sideBarItems = [
    {
        key: "dashboard",
        to: '/dashboard',
        text: 'Dashboard',
        icon: LayoutDashboard
    },
    {
        key: "profile",
        to: '/profile',
        text: 'Profile',
        icon: User
    }
];