import {
LuLayoutDashboard,
LuUsers,
LuClipboardCheck,
LuSquarePlus,
LuLogOut,
LuPanelRight,
LuSquareKanban
} from 'react-icons/lu'
import { Label } from 'recharts'

export const SIDE_MENU_DATA = [
    {
        id:"01",
        label:"Dashboard",
        icon: LuLayoutDashboard,
        path: "/admin/dashboard",
    },
    {
        id:"02",
        label:"Manage Bugs",
        icon: LuClipboardCheck,
        path: "/admin/manage",
    },
    {
        id:"03",
        label:"Create Project",
        icon: LuPanelRight,
        path: "/admin/form",
    },
    {
        id:"04",
        label:"Create Bug",
        icon: LuSquarePlus,
        path: "/admin/create-bug",
    },
    {
        id:"05",
        label:"Team Members",
        icon: LuUsers,
        path: "/admin/manageuser",
    },
    {
        id:"06",
        label:"Logout",
        icon: LuLogOut,
        path: "logout",
    }
]

export const SIDE_MENU_USER_DATA = [
    {
        id:"01",
        label:"Dashboard",
        icon: LuLayoutDashboard,
        path: "/user/dashboard",
    },
    {
        id:"02",
        label:"Manage Tasks",
        icon: LuClipboardCheck,
        path: "/user/all-bugs",
    },
    {
        id:"03",
        label:"Kanban Board",
        icon: LuSquareKanban,
        path: "/user/kanban",
    },
    
    {
        id:"05",
        label:"Logout",
        icon: LuLogOut,
        path: "logout",
    }




]

export const STATUS_DATA = [
    { label:"Pending", value:"Pending" },
    { label:"In Progress",value:"In Progress" },
    { label:"Completed", value:"Completed" },
]

export const PRIORITY_DATA = [
    { label:"Low", value:"Low" },
    { label:"Medium", value:"Medium" },
    { label:"High", value:"High" }
]