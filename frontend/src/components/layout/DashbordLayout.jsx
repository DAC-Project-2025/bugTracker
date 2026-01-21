import React from 'react'
import Navbar from './Navbar'
import SideMenu from './SideMenu'

const DashbordLayout = ({children, activeMenu}) => {
  const user = {}
  return (
    <div className=''>
      <Navbar activeMenu={activeMenu}/>
      {
        user && (<div className='flex'>
          <div>
            <SideMenu/>
          </div>
          <div className='grow mx-5'>{children}</div>
        </div>)
      }
    </div>
  )
}

export default DashbordLayout
