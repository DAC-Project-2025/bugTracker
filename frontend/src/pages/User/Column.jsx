import React from "react";
import { Draggable } from "@hello-pangea/dnd";
import Card from "./Card";
import { FaChevronUp, FaChevronDown } from "react-icons/fa";

export default function Column({ column, tasks, collapsed, onToggleCollapse }) {
  return (
    <div
      className={`bg-gray-100 rounded-lg shadow p-2 flex flex-col transition-all duration-300 ${
        collapsed ? "w-12" : "w-64"
      }`}
    >
      <div className="flex items-center justify-between mb-2">
        {collapsed ? (
          <div className="flex flex-col items-center justify-center">
            <h2
              className="font-semibold text-gray-700 text-xs origin-bottom-left"
              style={{
                transform: "rotate(-90deg)",
                whiteSpace: "nowrap",
                marginTop: "8px",
              }}
            >
              {column.title}
            </h2>
          </div>
        ) : (
          <>
            <h2 className="font-semibold text-gray-700 text-sm truncate">
              {column.title}
            </h2>
            <span className="ml-auto text-xs bg-blue-100 text-blue-800 rounded-full px-2 py-0.5">
              {tasks.length}
            </span>
          </>
        )}
        <button
          className="ml-2 p-1 text-gray-500 hover:text-gray-700"
          onClick={onToggleCollapse}
        >
          {collapsed ? <FaChevronDown /> : <FaChevronUp />}
        </button>
      </div>

      {!collapsed && (
        <div className="flex-1 overflow-y-auto">
          {tasks.map((task, index) => (
            <Draggable key={task.id} draggableId={task.id} index={index}>
              {(provided) => (
                <div
                  {...provided.draggableProps}
                  {...provided.dragHandleProps}
                  ref={provided.innerRef}
                >
                  <Card task={task} />
                </div>
              )}
            </Draggable>
          ))}
        </div>
      )}

      {!collapsed && (
        <button className="mt-2 bg-blue-500 text-white rounded p-1 text-xs hover:bg-blue-600">
          + Add Card
        </button>
      )}
    </div>
  );
}
