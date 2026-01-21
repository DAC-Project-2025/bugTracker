import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { FiPlus, FiCheck, FiTrash2 } from "react-icons/fi";

const ProjectsPage = ({ projectsByStatus }) => {
  const [activeTab, setActiveTab] = useState("active");
  const [hoveredTab, setHoveredTab] = useState(null);

  const navigate = useNavigate();

  // Animation variants for tab content
  const tabVariants = {
    hidden: { opacity: 0, y: 10 },
    visible: { opacity: 1, y: 0 },
    exit: { opacity: 0, y: -10 },
  };

  // Helper to get progress % based on project status
  const getProgressFromStatus = (status) => {
    switch (status) {
      case "OPEN":
        return 20;
      case "IN_PROGRESS":
        return 60;
      case "COMPLETED":
      case "CLOSED":
        return 100;
      default:
        return 0;
    }
  };

  // Get progress bar color
  const getProgressColor = (progress) => {
    if (progress < 30) return "bg-red-500";
    if (progress < 70) return "bg-yellow-500";
    if (progress < 90) return "bg-blue-500";
    return "bg-green-500";
  };

  // Underline effect for tabs
  const UnderlineEffect = ({ isActive }) => (
    <motion.div
      className={`absolute bottom-0 left-0 w-full h-0.5 ${
        isActive ? "bg-gradient-to-r from-blue-500 to-purple-600" : ""
      }`}
      initial={{ scaleX: 0 }}
      animate={{ scaleX: isActive ? 1 : 0 }}
      transition={{ duration: 0.3, ease: "easeInOut" }}
    />
  );

  // Projects list for the current tab
  const projectsForTab = projectsByStatus[activeTab] || [];

  // Handle project deletion
  const handleDeleteProject = async (projectId) => {
    const token = localStorage.getItem("jwt");
    const api = axios.create({
      baseURL: "http://localhost:8083",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    // useEffect(()=>{

    // },[])

    try {
      await api.delete(`/api/projects/${projectId}`);
      // Optionally, you can refresh the project list or remove the project from the state
      alert("Project deleted successfully.");
    } catch (error) {
      console.error("Failed to delete project", error);
      alert("Failed to delete project.");
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        <div className="bg-white rounded-xl shadow-lg overflow-hidden">
          {/* Tabs Navigation */}
          <div className="border-b border-gray-200">
            <nav className="flex -mb-px">
              {["active", "closed", "all"].map((tab) => (
                <button
                  key={tab}
                  className={`py-4 px-6 text-center border-b-2 font-medium text-sm relative ${
                    activeTab === tab
                      ? "text-blue-600"
                      : "text-gray-500 hover:text-gray-700 border-transparent"
                  }`}
                  onClick={() => setActiveTab(tab)}
                  onMouseEnter={() => setHoveredTab(tab)}
                  onMouseLeave={() => setHoveredTab(null)}
                >
                  {tab.charAt(0).toUpperCase() + tab.slice(1)} Projects
                  {(hoveredTab === tab || activeTab === tab) && (
                    <UnderlineEffect isActive={activeTab === tab} />
                  )}
                </button>
              ))}
            </nav>
          </div>

          {/* Tab Content */}
          <div className="p-6">
            <motion.div
              key={activeTab}
              initial="hidden"
              animate="visible"
              exit="exit"
              variants={tabVariants}
              transition={{ duration: 0.3 }}
              className="grid gap-6 md:grid-cols-2 lg:grid-cols-3"
            >
              {projectsForTab.length > 0 ? (
                projectsForTab.map((project) => {
                  const progress = getProgressFromStatus(project.status);
                  const image =
                    project.image ||
                    "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/6d152ce1-dd08-4de8-b747-370c87456264.png";
                  const alt = project.title || "Project image";

                  return (
                    <motion.div
                      key={project.id}
                      whileHover={{ y: -5 }}
                      className="bg-white rounded-lg border border-gray-200 hover:shadow-md transition-shadow duration-300 overflow-hidden"
                    >
                      <div className="relative h-40 overflow-hidden">
                        <img
                          src={image}
                          alt={alt}
                          className="w-full h-full object-cover"
                          onError={(e) => {
                            e.target.src =
                              "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/6d152ce1-dd08-4de8-b747-370c87456264.png";
                            e.target.alt = "Placeholder for missing project image";
                          }}
                        />
                        {activeTab === "active" && (
                          <div className="absolute bottom-0 left-0 right-0 px-4 py-2 bg-black bg-opacity-50 text-white text-sm font-medium">
                            <span>{project.category}</span>
                          </div>
                        )}
                      </div>
                      <div className="p-5">
                        <h3 className="text-xl font-bold text-gray-900 mb-2">
                          {project.title}
                        </h3>
                        <p className="text-gray-600 mb-4">{project.description}</p>

                        {activeTab === "active" && (
                          <div className="space-y-2">
                            <div className="text-sm text-gray-500 flex justify-between">
                              <span>Progress</span>
                              <span>{progress}%</span>
                            </div>
                            <div className="w-full bg-gray-200 rounded-full h-2.5">
                              <div
                                className={`h-2.5 rounded-full ${getProgressColor(
                                  progress
                                )}`}
                                style={{ width: `${progress}%` }}
                              ></div>
                            </div>
                          </div>
                        )}

                        {activeTab === "closed" && (
                          <div className="flex items-center text-green-500">
                            <svg
                              className="w-5 h-5 mr-1"
                              fill="none"
                              stroke="currentColor"
                              viewBox="0 0 24 24"
                              xmlns="http://www.w3.org/2000/svg"
                            >
                              <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth="2"
                                d="M5 13l4 4L19 7"
                              ></path>
                            </svg>
                            <span className="font-medium">Completed</span>
                          </div>
                        )}

                        <div className="mt-4 flex justify-between">
                          <button
                            className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                            onClick={() =>
                              navigate(`/admin/project/${project.id}`, {
                                state: { project },
                              })
                            }
                          >
                            View Details
                          </button>
                          <button
                            className="text-red-600 hover:text-red-900 text-sm font-medium"
                            onClick={() => handleDeleteProject(project.id)}
                          >
                            <FiTrash2 className="inline-block" /> Delete
                          </button>
                        </div>
                      </div>
                    </motion.div>
                  );
                })
              ) : (
                <motion.div
                  className="text-center py-12"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: 0.3 }}
                >
                  <img
                    src="https://storage.googleapis.com/workspace-0f70711f-8e73-4a1e-8593-83c9fb818ac2.png"
                    alt="No projects found illustration showing an empty folder and a magnifying glass"
                    className="mx-auto mb-4"
                  />
                  <h3 className="text-lg font-medium text-gray-900">
                    No {activeTab} projects found
                  </h3>
                  <p className="text-gray-500 mt-1">
                    You don't have any {activeTab} projects at the moment
                  </p>
                </motion.div>
              )}
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProjectsPage;
