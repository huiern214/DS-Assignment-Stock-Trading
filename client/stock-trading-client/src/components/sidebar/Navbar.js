import React, { useState } from 'react';
import * as FaIcons from 'react-icons/fa';
import * as AiIcons from 'react-icons/ai';
import * as IoIcons from 'react-icons/io';
import { Link, useLocation } from 'react-router-dom';
import { SidebarData } from './SidebarData';
import { IconContext } from 'react-icons';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../../redux/user/userActions';
import './Navbar.css';

function Navbar() {
  const [sidebar, setSidebar] = useState(false);
  const location = useLocation();
  const dispatch = useDispatch();
  const userId = useSelector((state) => state.user.userId);

  const handleLogout = () => {
    dispatch(logout());
  };

  const showSidebar = () => setSidebar(!sidebar);

  const renderSidebarData = () => {
    if (location.pathname === '/login') {
      // login page
      return [
        {
          title: 'Sign in',
          path: '/login',
          icon: <FaIcons.FaSignInAlt />,
          cName: 'nav-text'
        },
        {
          title: 'Support',
          path: '/support',
          icon: <IoIcons.IoMdHelpCircle />,
          cName: 'nav-text'
        }
      ];
    } else if (userId < 0) {
      // Admin user
      return [
        {
          title: 'User Management',
          path: '/user_management',
          icon: <FaIcons.FaUsers />,
          cName: 'nav-text'
        },
        {
          title: 'Stock Management',
          path: '/stock_management',
          icon: <AiIcons.AiOutlineStock />,
          cName: 'nav-text'
        },
        {
          title: 'Log Out',
          path: '/login',
          icon: <IoIcons.IoMdLogOut />,
          cName: 'nav-text',
          onClick: handleLogout
        },
      ]; 
    } else {
      // Non-admin user
      return [
        ...SidebarData,
        {
          title: 'Log Out',
          path: '/login',
          icon: <IoIcons.IoMdLogOut />,
          cName: 'nav-text',
          onClick: handleLogout
        },
      ];
    }
  };

  return (
    <>
      <IconContext.Provider value={{ color: '#fff' }}>
        <div className='navbar'>
          <Link to='#' className='menu-bars'>
            <FaIcons.FaBars onClick={showSidebar} />
          </Link>
        </div>
        <nav className={sidebar ? 'nav-menu active' : 'nav-menu'}>
          <ul className='nav-menu-items' onClick={showSidebar}>
            <li className='navbar-toggle'>
              <Link to='#' className='menu-bars'>
                <AiIcons.AiOutlineClose />
              </Link>
            </li>
            {renderSidebarData().map((item, index) => {
              return (
                <li key={index} className={item.cName}>
                  <Link to={item.path} onClick={item.onClick}>
                    {item.icon}
                    <span>{item.title}</span>
                  </Link>
                </li>
              );
            })}
          </ul>
        </nav>
      </IconContext.Provider>
    </>
  );
}

export default Navbar;
