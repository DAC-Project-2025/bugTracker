import axios from "axios";
import { useState, useEffect } from "react";
import {
  FiPlus,
  FiX,
  FiChevronDown,
  FiUser,
  FiCpu,
  FiCalendar,
  FiFlag,
} from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import DashbordLayout from "./layout/DashbordLayout";

const AddProjectForm = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    category: "Web Development",
    startDate: "",
    deadline: "",
    status: "OPEN",
    priority: "medium",
    technologies: [],
    teamMembers: [],
  });

  const [currentTech, setCurrentTech] = useState("");
  const [currentMember, setCurrentMember] = useState("");
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [availableUsers, setAvailableUsers] = useState([]);

  const categories = [
    "Web Development",
    "Mobile Development",
    "UI/UX Design",
    "DevOps",
    "Data Science",
    "Marketing",
  ];

  // Fetch available team members from the backend
  useEffect(() => {

    const token = localStorage.getItem("jwt")
    if(!token){
      return 
    }

    const fetchUsers = async () => {
      // Replace with your API call

      //http://localhost:8081/api/user
      const token = localStorage.getItem("jwt");
      const api = axios.create({
        baseURL: "http://localhost:8081",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      try {
        const response = await api.get("/api/user");
        const name = response.data.map((user) => user.name);
        setAvailableUsers(name);
      } catch (error) {
        console.log("failed to fetch user data", error);
      }
      // const response = await fetch('/api/users'); // Example API endpoint
      // const data = await response.json();
      // setAvailableUsers(data);
    };

    fetchUsers();
  }, []);

  // Technology handlers
  const handleTechKeyDown = (e) => {
    if (["Enter", "Tab", ","].includes(e.key) && currentTech.trim()) {
      e.preventDefault();
      if (!formData.technologies.includes(currentTech.trim())) {
        setFormData({
          ...formData,
          technologies: [...formData.technologies, currentTech.trim()],
        });
      }
      setCurrentTech("");
    }
  };

  const removeTech = (techToRemove) => {
    setFormData({
      ...formData,
      technologies: formData.technologies.filter(
        (tech) => tech !== techToRemove
      ),
    });
  };

  // Team member handlers
  const handleMemberChange = (e) => {
    const selectedMember = e.target.value;
    if (selectedMember && !formData.teamMembers.includes(selectedMember)) {
      setFormData({
        ...formData,
        teamMembers: [...formData.teamMembers, selectedMember],
      });
      setCurrentMember("");
    }
  };

  const removeMember = (memberToRemove) => {
    setFormData({
      ...formData,
      teamMembers: formData.teamMembers.filter(
        (member) => member !== memberToRemove
      ),
    });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: null,
      });
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.title.trim()) newErrors.title = "Title is required";
    if (!formData.description.trim())
      newErrors.description = "Description is required";
    if (!formData.startDate) newErrors.startDate = "Start date is required";
    if (!formData.deadline) newErrors.deadline = "Deadline is required";
    if (
      formData.deadline &&
      formData.startDate &&
      new Date(formData.deadline) < new Date(formData.startDate)
    ) {
      newErrors.deadline = "Deadline must be after start date";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async(e) => {
    e.preventDefault();
    if (validateForm()) {
      setIsSubmitting(true);
      // Here you would typically call an API to create the project

      const token =  localStorage.getItem("jwt")
      const api = axios.create({
        baseURL: "http://localhost:8083",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      })

      const response = await api.post("/api/projects",formData);
      console.log("Project created",response)
      setTimeout(() => {
        console.log("Project created:", formData);
        setIsSubmitting(false);
        navigate("/admin/dashboard"); // Redirect after creation
      }, 500);
    }
  };

  return (

    <DashbordLayout >
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-2xl mx-auto px-4 py-8">
        {/* Header with back button */}
        <div className="mb-8 flex items-center">
          <h1 className="text-2xl font-bold text-gray-900">
            Create New Project
          </h1>
        </div>

        {/* Modern form container */}
        <div className="bg-white rounded-2xl shadow-md overflow-hidden">
          <form onSubmit={handleSubmit} className="p-6">
            {/* Title Section */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Project Title
              </label>
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                  errors.title ? "border-red-500" : "border-gray-300"
                }`}
                placeholder="Project name"
              />
              {errors.title && (
                <p className="mt-1 text-sm text-red-600">{errors.title}</p>
              )}
            </div>

            {/* Description Section */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Description
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows={3}
                className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                  errors.description ? "border-red-500" : "border-gray-300"
                }`}
                placeholder="Describe your project..."
              />
              {errors.description && (
                <p className="mt-1 text-sm text-red-600">
                  {errors.description}
                </p>
              )}
            </div>

            {/* Category and Status */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              {/* Category */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <FiCpu className="inline mr-2" />
                  Category
                </label>
                <select
                  name="category"
                  value={formData.category}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  {categories.map((category) => (
                    <option key={category} value={category}>
                      {category}
                    </option>
                  ))}
                </select>
              </div>

              {/* Status */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <FiFlag className="inline mr-2" />
                  Status
                </label>
                <div className="flex space-x-4">
                  <label className="flex items-center">
                    <input
                      type="radio"
                      name="status"
                      value="active"
                      checked={formData.status === "active"}
                      onChange={handleChange}
                      className="h-4 w-4 text-blue-600 focus:ring-blue-500"
                    />
                    <span className="ml-2">Active</span>
                  </label>
                  <label className="flex items-center">
                    <input
                      type="radio"
                      name="status"
                      value="completed"
                      checked={formData.status === "completed"}
                      onChange={handleChange}
                      className="h-4 w-4 text-blue-600 focus:ring-blue-500"
                    />
                    <span className="ml-2">Completed</span>
                  </label>
                </div>
              </div>
            </div>

            {/* Technologies Section */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Technologies
                <span className="text-xs text-gray-500 ml-1">
                  (press Enter to add)
                </span>
              </label>
              <div className="flex flex-wrap gap-2 border rounded-lg p-2 border-gray-300">
                {formData.technologies.map((tech) => (
                  <div
                    key={tech}
                    className="inline-flex items-center px-3 py-1 rounded-full bg-blue-50 text-blue-700"
                  >
                    {tech}
                    <button
                      type="button"
                      onClick={() => removeTech(tech)}
                      className="ml-2 text-blue-500 hover:text-blue-700"
                    >
                      <FiX className="w-3 h-3" />
                    </button>
                  </div>
                ))}
                <input
                  type="text"
                  value={currentTech}
                  onChange={(e) => setCurrentTech(e.target.value)}
                  onKeyDown={handleTechKeyDown}
                  className="flex-grow px-2 py-1 bg-transparent outline-none min-w-[100px]"
                  placeholder="React, Node, etc."
                />
              </div>
            </div>

            {/* Team Members Section */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                <FiUser className="inline mr-2" />
                Team Members
                <span className="text-xs text-gray-500 ml-1">
                  (select from available members)
                </span>
              </label>
              <select
                value={currentMember}
                onChange={handleMemberChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">Select a member</option>
                {availableUsers.map((user, idx) => (
                  <option key={idx} value={user}>
                    {user}
                  </option>
                ))}
              </select>
              <div className="flex flex-wrap gap-2 border rounded-lg p-2 border-gray-300 mt-2">
                {formData.teamMembers.map((member) => (
                  <div
                    key={member}
                    className="inline-flex items-center px-3 py-1 rounded-full bg-green-50 text-green-700"
                  >
                    {member}
                    <button
                      type="button"
                      onClick={() => removeMember(member)}
                      className="ml-2 text-green-500 hover:text-green-700"
                    >
                      <FiX className="w-3 h-3" />
                    </button>
                  </div>
                ))}
              </div>
            </div>

            {/* Date and Priority Sections */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              {/* Start Date */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <FiCalendar className="inline mr-2" />
                  Start Date
                </label>
                <input
                  type="date"
                  name="startDate"
                  value={formData.startDate}
                  onChange={handleChange}
                  className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                    errors.startDate ? "border-red-500" : "border-gray-300"
                  }`}
                />
                {errors.startDate && (
                  <p className="mt-1 text-sm text-red-600">
                    {errors.startDate}
                  </p>
                )}
              </div>

              {/* Deadline */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <FiCalendar className="inline mr-2" />
                  Deadline
                </label>
                <input
                  type="date"
                  name="deadline"
                  value={formData.deadline}
                  onChange={handleChange}
                  className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                    errors.deadline ? "border-red-500" : "border-gray-300"
                  }`}
                />
                {errors.deadline && (
                  <p className="mt-1 text-sm text-red-600">{errors.deadline}</p>
                )}
              </div>
            </div>

            {/* Priority Section */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Priority
              </label>
              <div className="grid grid-cols-3 gap-3">
                <label
                  className={`p-4 border rounded-lg cursor-pointer ${
                    formData.priority === "low"
                      ? "bg-green-50 border-green-500"
                      : "border-gray-300"
                  }`}
                >
                  <input
                    type="radio"
                    name="priority"
                    value="low"
                    checked={formData.priority === "low"}
                    onChange={handleChange}
                    className="sr-only"
                  />
                  <div className="flex flex-col items-center">
                    <div className="w-4 h-4 rounded-full bg-green-500 mb-2"></div>
                    <span>Low</span>
                  </div>
                </label>
                <label
                  className={`p-4 border rounded-lg cursor-pointer ${
                    formData.priority === "medium"
                      ? "bg-yellow-50 border-yellow-500"
                      : "border-gray-300"
                  }`}
                >
                  <input
                    type="radio"
                    name="priority"
                    value="medium"
                    checked={formData.priority === "medium"}
                    onChange={handleChange}
                    className="sr-only"
                  />
                  <div className="flex flex-col items-center">
                    <div className="w-4 h-4 rounded-full bg-yellow-500 mb-2"></div>
                    <span>Medium</span>
                  </div>
                </label>
                <label
                  className={`p-4 border rounded-lg cursor-pointer ${
                    formData.priority === "high"
                      ? "bg-red-50 border-red-500"
                      : "border-gray-300"
                  }`}
                >
                  <input
                    type="radio"
                    name="priority"
                    value="high"
                    checked={formData.priority === "high"}
                    onChange={handleChange}
                    className="sr-only"
                  />
                  <div className="flex flex-col items-center">
                    <div className="w-4 h-4 rounded-full bg-red-500 mb-2"></div>
                    <span>High</span>
                  </div>
                </label>
              </div>
            </div>

            {/* Form Actions */}
            <div className="flex justify-between border-t pt-6">
              <button
                type="button"
                onClick={() => navigate(-1)}
                className="px-6 py-3 rounded-lg text-gray-700 hover:bg-gray-100 transition"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-70 transition flex items-center"
                disabled={isSubmitting}
              >
                {isSubmitting ? (
                  <>
                    <svg
                      className="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                    >
                      <circle
                        className="opacity-25"
                        cx="12"
                        cy="12"
                        r="10"
                        stroke="currentColor"
                        strokeWidth="4"
                      ></circle>
                      <path
                        className="opacity-75"
                        fill="currentColor"
                        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                      ></path>
                    </svg>
                    Creating...
                  </>
                ) : (
                  <>
                    <FiPlus className="mr-2" />
                    Create Project
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    </DashbordLayout>
  );
};

export default AddProjectForm;
