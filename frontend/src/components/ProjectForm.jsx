import React from 'react'
import AddProjectForm from './AddProjectForm'
import Navbar from './layout/Navbar'
import SideMenu from './layout/SideMenu'

function ProjectForm() {
  return (
    <div>
    <div className="">
      <Navbar activeMenu={"dashboard"}/>
      <SideMenu/>
        <AddProjectForm/>
    </div>
    </div>
  )
}

export default ProjectForm
