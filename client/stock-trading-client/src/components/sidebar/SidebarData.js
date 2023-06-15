import React from 'react';
import * as FaIcons from 'react-icons/fa';
import * as AiIcons from 'react-icons/ai';
import * as IoIcons from 'react-icons/io';

export const SidebarData = [
  {
    title: 'Home',
    path: '/stocks',
    icon: <AiIcons.AiFillHome />,
    cName: 'nav-text'
  },
  {
    title: 'Order',
    path: '/order',
    icon: <FaIcons.FaCartPlus />,
    cName: 'nav-text'
  },
  {
    title: 'Dashboard',
    path: '/dashboard',
    icon: <IoIcons.IoIosAnalytics />,
    cName: 'nav-text'
  },
  {
    title: 'News',
    path: '/news',
    icon: <IoIcons.IoMdNotifications />,
    cName: 'nav-text'
  },
  {
    title: 'Messages',
    path: '/messages',
    icon: <FaIcons.FaEnvelopeOpenText />,
    cName: 'nav-text'
  },
  {
    title: 'Profile',
    path: '/profile',
    icon: <IoIcons.IoMdPerson />,
    cName: 'nav-text'
  },
  // {
  //   title: 'Log Out',
    // path: '/login',
  //   icon: <IoIcons.IoMdLogOut />,
  //   cName: 'nav-text',
  // }
];